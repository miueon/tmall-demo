package com.tmallspringboot.demo.service;

import com.tmallspringboot.demo.dao.CategoryDAO;
import com.tmallspringboot.demo.pojo.Category;
import com.tmallspringboot.demo.pojo.Product;
import com.tmallspringboot.demo.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "categories")
public class CategoryService {
    @Autowired
    CategoryDAO categoryDAO;

    @Cacheable(key = "'categories-all'")
    public List<Category> list() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    @Cacheable(key = "'categories-page'+#p0+'-'+#p1")
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start,size , sort); // Pageable defines how much entity to show
        Page pageFromJPA = categoryDAO.findAll(pageable);// require pages from categoryDAO by pageable
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    @CacheEvict(allEntries = true) // delete all of the relate caches for consistency
    public void add(Category bean) {
        categoryDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        categoryDAO.deleteById(id);
    }

    // the cacheTable is like:
    // categories:{'categories-one-#p0': ,'categories-page-'+#p0+ '-' + #p1' : []}

    @Cacheable(key = "'categories-one-'+ #p0")
    public Category get(int id) {
        Optional<Category> c = categoryDAO.findById(id);
        return c.orElse(null);
    }

    @CacheEvict(allEntries = true)
    public void update(Category bean) {
        categoryDAO.save(bean);
    }

    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }
    // When the RESTController converts the category pojo, it will recursively convert array inside
    // the pojo. however, the list contains product, and the product contains category
    // so if we don't remove the category from product, the program will crush by infinite loop.

    public void removeCategoryFromProduct(Category category) {
        List<Product> products =category.getProducts();
        if(null!=products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }

        List<List<Product>> productsByRow =category.getProductsByRow();
        if(null!=productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p: ps) {
                    p.setCategory(null);
                }
            }
        }
    }
}
