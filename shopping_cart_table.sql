-- 购物信息表 (shopping_cart)
CREATE TABLE IF NOT EXISTS shoppingmall_lixiong.shopping_cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_product_id (product_id)
);