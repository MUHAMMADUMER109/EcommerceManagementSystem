package com.ecommerce.management.Decorator;

import com.ecommerce.management.Model.ProductModel;

// DECORATOR PATTERN


// Abstract interface
interface IProduct {
    String getName();
    double getPrice();
}

// Concrete base class
class BaseProduct implements IProduct {
    private final ProductModel product;

    public BaseProduct(ProductModel product) {
        this.product = product;
    }

    @Override
    public String getName() {
        return product.getName();
    }

    @Override
    public double getPrice() {
        return product.getPrice();
    }
}

// Base decorator
class ProductDecorator implements IProduct {
    protected final IProduct decoratedProduct;

    public ProductDecorator(IProduct product) {
        this.decoratedProduct = product;
    }

    @Override
    public String getName() {
        return decoratedProduct.getName();
    }

    @Override
    public double getPrice() {
        return decoratedProduct.getPrice();
    }
}

// Concrete decorator
class DiscountedProduct extends ProductDecorator {
    private final double discountPercent;

    public DiscountedProduct(IProduct product, double discountPercent) {
        super(product);
        this.discountPercent = discountPercent;
    }

    @Override
    public String getName() {
        return decoratedProduct.getName() + " (" + discountPercent + "% OFF)";
    }

    @Override
    public double getPrice() {
        double discounted = decoratedProduct.getPrice() * (1 - discountPercent / 100);
        return Math.round(discounted * 100.0) / 100.0;
    }
}

// Main DiscountDecorator used by ProductService
public class DiscountDecorator {

    private final ProductModel product;
    private final double discountPercent;

    public DiscountDecorator(ProductModel product, double discountPercent) {
        this.product         = product;
        this.discountPercent = discountPercent;
    }

    public ProductModel decorate() {
        // Step 1 Wrap in BaseProduct
        IProduct base = new BaseProduct(product);
        System.out.println("Base product: " + base.getName()
                + " | Price: Rs. " + base.getPrice());

        // Step 2 Wrap in DiscountedProduct
        IProduct discounted = new DiscountedProduct(base, discountPercent);
        System.out.println("Decorated product: " + discounted.getName()
                + " | New Price: Rs. " + discounted.getPrice());

        // Step 3 discounted price on original model
        product.setDiscountedPrice(discounted.getPrice());
        return product;
    }

    // Static helper  used by ProductService
    public static ProductModel applyDiscount(ProductModel product,
                                             double discountPercent) {
        return new DiscountDecorator(product, discountPercent).decorate();
    }

    // No auto bulk discount  only manual from admin
    public static ProductModel applyBulkDiscount(ProductModel product) {
        return product;
    }
}