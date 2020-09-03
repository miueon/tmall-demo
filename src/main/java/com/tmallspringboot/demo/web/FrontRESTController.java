package com.tmallspringboot.demo.web;

import com.tmallspringboot.demo.pojo.*;
import com.tmallspringboot.demo.realm.JPARealm;
import com.tmallspringboot.demo.service.*;
import com.tmallspringboot.demo.util.Result;
import lombok.val;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class FrontRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    UserService userService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    OrderService orderService;

    @GetMapping("/fronthome")
    public List<Category> home() {
        var categories = categoryService.list();
        productService.fill(categories);
        productService.fillByRow(categories);
        categoryService.removeCategoryFromProduct(categories);
        return categories;
    }

    @PostMapping("/frontregister")
    public Result register(@RequestBody User user) {
        String name = user.getName();
        String password = user.getPassword();// TODO: add cryptor in frontend
        name = HtmlUtils.htmlEscape(name);

        user.setName(name);
        boolean exist = userService.isExist(name);

        if (exist) {
            String message = "This userName has been used";
            return Result.fail(message);
        }

        JPARealm.authenticationHelper(user, password, userService);

        return Result.success();
    }

    @PostMapping("/frontlogin")
    public Result login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        // User user = userService.get(name, userParam.getPassword()); deprecated
        val subject = SecurityUtils.getSubject();
        val token = new UsernamePasswordToken(name, userParam.getPassword());
        try {
            subject.login(token);
            val usr = userService.getByName(name);
            subject.getSession().setAttribute("user", usr);
            return Result.success();
        } catch (AuthenticationException e) {
            String msg = "User name or password incorrect!";
            return Result.fail(msg);
        }
    }

    @GetMapping("/frontproduct/{pid}")
    public Result product(@PathVariable("pid") int pid) {
        Product product = productService.get(pid);
        var productSingleImages = productImageService.listSingleProductImages(product);
        var productDetailImages = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);

        var pvs = propertyValueService.list(product);
        var reviews = reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProductImage(product);

        Map<String, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    @GetMapping("frontcheckLogin")
    public Result checkLogin() {
//        User user = (User) session.getAttribute("user");
//        if (user == null) {
//            return Result.fail("Login to continue");
//        }
        val subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return Result.success();
        } else {
            return Result.fail("Login to continue");
        }
    }

    @GetMapping("frontcategory/{cid}")
    public Category category(@PathVariable("cid") int cid, String sort) {
        Category c = categoryService.get(cid);
        if (sort != null && !sort.equals("null")) {
            // System.out.println(sort.length());
            productService.fillByOption(c, sort);
        } else {
            productService.fill(c);
            productService.setSaleAndReviewNumber(c.getProducts());
        }
        categoryService.removeCategoryFromProduct(c);
        return c;
    }

    @PostMapping("frontsearch")
    public List<Product> search(String keyword) {
        if (keyword == null || keyword.equals("null")) {
            keyword = "";
        }
        var ps = productService.search(keyword, 0, 20);
        productImageService.setFirstProductImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }

    @GetMapping("frontbuyone")
    public Integer buyOne(int pid, int num, HttpSession session) {
        return buyOneAndAddCart(pid, num, session);
    }

    public int buyOneAndAddCart(int pid, int num, HttpSession session) {
        var product = productService.get(pid);
        AtomicInteger oiid = new AtomicInteger();
        var usr = (User) session.getAttribute("user");
        AtomicBoolean found = new AtomicBoolean(false);
        var ois = orderItemService.listByUser(usr);
        ois.parallelStream().forEach(orderItem -> {
            if (orderItem.getProduct().getId() == pid) {
                orderItem.setNumber(orderItem.getNumber() + num);
                orderItemService.update(orderItem);
                found.set(true);
                oiid.set(orderItem.getId());
            }
        });
        if (!found.get()) {
            var oi = new OrderItem();
            oi.setUser(usr);
            oi.setProduct(product);
            oi.setNumber(num);
            orderItemService.add(oi);
            oiid.set(oi.getId());
        }
        return oiid.get();
    }

    @GetMapping("frontbuy")
    public Result buy(String[] oiid, HttpSession session) {
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;
        for (var strid : oiid) {
            int id = Integer.parseInt(strid);
            var oi = orderItemService.get(id);
            total += oi.getProduct().getPromotedPrice() * oi.getNumber();
            orderItems.add(oi);
        }

        productImageService.setFirstProductImageForOrderItems(orderItems);
        session.setAttribute("ois", orderItems);
        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);
    }

    @GetMapping("frontaddCart")
    public Result addCart(int pid, int num, HttpSession session) {
        buyOneAndAddCart(pid, num, session);
        return Result.success();
    }

    @GetMapping("frontcart")
    public List<OrderItem> cart(HttpSession session) {
        var usr = (User) session.getAttribute("user");
        var ois = orderItemService.listByUser(usr);
        productImageService.setFirstProductImageForOrderItems(ois);
        return ois;
    }

    @GetMapping("frontchangeOrderItem")
    public Result changeOrderItem(HttpSession session, int pid, int num) {
        var usr = (User) session.getAttribute("user");
        if ((usr == null)) {
            return Result.fail("Login to continue");
        }
        var ois = orderItemService.listByUser(usr);
        ois.parallelStream()
                .forEach(orderItem -> {
                    if (orderItem.getProduct().getId() == pid) {
                        orderItem.setNumber(num);
                        orderItemService.update(orderItem);
                    }
                });
        return Result.success();
    }

    @GetMapping("frontdeleteOrderItem")
    public Result deleteOrderItem(HttpSession session, int oiid) {
        var usr = (User) session.getAttribute("user");
        if ((usr == null)) {
            return Result.fail("Login to continue");
        }
        orderItemService.delete(oiid);
        return Result.success();
    }

    @PostMapping("frontcreateOrder")
    public Result createOrder(@RequestBody Order order, HttpSession session) {
        var usr = (User) session.getAttribute("user");
        if (usr == null) {
            return Result.fail("Login to continue");
        }
        var orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS")
                .format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(usr);
        order.setStatus(OrderService.waitPay);
        var ois = (List<OrderItem>) session.getAttribute("ois");
        var total = orderService.add(order, ois);
        Map<String, Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);
        return Result.success(map);
    }

    @GetMapping("frontpayed")
    public Object payed(int oid) {
        var order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }

    @GetMapping("frontbought")
    public Object bought(HttpSession session) {
        var usr = (User) session.getAttribute("user");
        if (usr == null) {
            return Result.fail("Login to continue");
        }
        var os = orderService.listByUserAndNotDeleted(usr);
        //System.out.println(os.get(0).getId());
        orderService.removeOrderFromOrderItem(os);
        return os;
    }

    @GetMapping("frontconfirmPay")
    public Object confirmPay(int oid) {
        var order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.calc(order);
        orderService.removeOrderFromOrderItem(order);
        return order;
    }

    @GetMapping("frontorderConfirmed")
    public Object orderConfirmed(int oid) {
        var o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }

    @PutMapping("frontdeleteOrder")
    public Object deleteOrder(int oid) {
        var o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }

    @GetMapping("frontreview")
    public Object review(int oid) {
        var o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.removeOrderFromOrderItem(o);
        var p = o.getOrderItems().get(0).getProduct();
        var reviews = reviewService.list(p);
        productService.setSaleAndReviewNumber(p);
        Map<String , Object> map = new HashMap<>();
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    @PostMapping("frontdoreview")
    public Object postReview(HttpSession session, int oid, int pid, String content) {
        var o = orderService.get(oid);
        o.setStatus(OrderService.finish);
        orderService.update(o);

        var p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        var usr = (User) session.getAttribute("user");
        var review = new Review();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(usr);
        reviewService.add(review);

        return Result.success();
    }

}
