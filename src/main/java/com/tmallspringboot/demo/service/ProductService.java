package com.tmallspringboot.demo.service;


import com.tmallspringboot.demo.comparator.ProductAllComparator;
import com.tmallspringboot.demo.comparator.ProductReviewComparator;
import com.tmallspringboot.demo.comparator.ProductSaleCountComparator;
import com.tmallspringboot.demo.dao.ProductDAO;
import com.tmallspringboot.demo.es.ProductESDAO;
import com.tmallspringboot.demo.pojo.Category;
import com.tmallspringboot.demo.pojo.Product;
import com.tmallspringboot.demo.util.Page4Navigator;
import org.apache.lucene.search.ScoreMode;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(keyGenerator = "keyGenerator", cacheNames = "product")
public class ProductService {
    @Autowired
    ProductDAO productDAO;
    @Autowired
    ProductESDAO productESDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;

    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    @CacheEvict(allEntries = true)
    public void add(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productDAO.deleteById(id);
        productESDAO.deleteById(id);
    }


    @Cacheable
    public Product get(int id) {
        return productDAO.findById(id).orElse(null);
    }

    @CacheEvict(allEntries = true)
    public void update(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    private void initDatabase2ES() {
        Sort sort;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> page = productESDAO.findAll(pageable);
        if (page.getContent().isEmpty()) {
            List<Product> products = productDAO.findAll();
            for (Product product : products) {
                productESDAO.save(product);
            }
        }
    }

    @Cacheable
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start, size, sort);
        Page<Product> pageFromJPA = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    @Cacheable
    public List<Product> search(String keyword, int start, int size) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "id");
//        Pageable pageable = PageRequest.of(start, size, sort);
//        var ps = productDAO.findByNameLike("%" + keyword + "%", pageable);
//        return ps;
        initDatabase2ES();
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(
                QueryBuilders.matchPhraseQuery("name", keyword),
                ScoreFunctionBuilders.weightFactorFunction(100)
        ).scoreMode(FunctionScoreQuery.ScoreMode.SUM).setMinScore(10);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start, size, sort);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();
        Page<Product> page = productESDAO.search(searchQuery);
        return page.getContent();
    }


    public void fill(List<Category> categorys) {
        // fill the transient data inside categories
        for (Category category : categorys) {
            fill(category);
        }
    }

    public void fill(Category category) {
        List<Product> products = listByCategory(category);
        productImageService.setFirstProductImages(products);
        category.setProducts(products);
    }

    public void fillByOption(Category category, String option) {
        switch (option) {
            case "review":
                category.setProducts(listByCategoryReviewFirst(category));
                break;
            case "date":
                category.setProducts(listByCategoryNewFirst(category));
                break;
            case "saleCount":
                category.setProducts(listByCategorySaleFirst(category));
                break;
            case "price":
                category.setProducts(listByCategoryLowPriceFirst(category));
                break;
            case "all":
                category.setProducts(listByCategoryAll(category));
                break;
        }
        productImageService.setFirstProductImages(category.getProducts());
    }

    public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8; // default Row size
        for (Category category : categorys) {
            List<Product> products = category.getProducts(); // this products size as first priority
            List<List<Product>> productsByRow = new ArrayList<>();
            for (int i = 0; i < products.size(); i += productNumberEachRow) {
                int size = i + productNumberEachRow;
                size = Math.min(size, products.size());
                List<Product> productsOfEachRow = products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    @Cacheable
    public List<Product> listByCategory(Category category) {
        return productDAO.findByCategoryOrderById(category);
    }

    @Cacheable
    public List<Product> listByCategoryWithReviewAndSale(Category category) {
        var ps = listByCategory(category);
        setSaleAndReviewNumber(ps);
        return ps;
    }

    @Cacheable
    public List<Product> listByCategoryLowPriceFirst(Category category) {
        var ps =  productDAO.findByCategoryOrderByPromotedPrice(category);
        setSaleAndReviewNumber(ps);
        return ps;
    }

    @Cacheable
    public List<Product> listByCategoryNewFirst(Category category) {
        var ps = productDAO.findByCategoryOrderByCreateDateDesc(category);
        setSaleAndReviewNumber(ps);
        return ps;
    }


    @Cacheable
    public List<Product> listByCategorySaleFirst(Category category) {
        var products = listByCategoryWithReviewAndSale(category);
        products.sort(new ProductSaleCountComparator());
        return products;
    }

    @Cacheable
    public List<Product> listByCategoryReviewFirst(Category category) {
        var products = listByCategoryWithReviewAndSale(category);
        products.sort(new ProductReviewComparator());
        return products;
    }
    @Cacheable
    public List<Product> listByCategoryAll(Category category) {
        var products = listByCategoryWithReviewAndSale(category);
        products.sort(new ProductAllComparator());
        return products;
    }

    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);

    }

    public void setSaleAndReviewNumber(List<Product> products) {
        products.forEach(this::setSaleAndReviewNumber);
    }
}
