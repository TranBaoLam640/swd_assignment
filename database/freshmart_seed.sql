-- =====================================================================
-- FreshMart — database + schema + seed data for manual/functional testing
-- =====================================================================
--
-- HOW TO USE (recommended, safest order):
--   1) Run the "1) CREATE DATABASE" section below.
--   2) Start the backend ONCE against this empty database
--      (ddl-auto: update in application.yaml will let Hibernate create
--      every table exactly matching the JPA entities — this avoids any
--      risk of this hand-written script's column types drifting from
--      what Hibernate actually expects). Stop the backend again.
--   3) Come back and run ONLY the "3) SEED DATA" section below.
--
-- The "2) CREATE TABLES" section is included for reference/inspection
-- (e.g. if you want to look at the schema in MySQL Workbench before ever
-- running the app), and as a fallback if you'd rather not run the app
-- first. If you do use it, run the WHOLE script in order (1 -> 2 -> 3).
--
-- Login credentials for every seeded user below: password is
--   Test@1234
-- (bcrypt-hashed already, ready to log in with via /login — no need to
-- go through /register for these).
-- =====================================================================


-- =====================================================================
-- 1) CREATE DATABASE
-- =====================================================================
CREATE DATABASE IF NOT EXISTS freshmart_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE freshmart_db;


-- =====================================================================
-- 2) CREATE TABLES (skip this section if you let Hibernate create the
--    schema instead — see step 2 above. Safe to re-run: drops first.)
-- =====================================================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS product_review;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS shop;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS role;

SET FOREIGN_KEY_CHECKS = 1;

-- Note: none of these tables have real FOREIGN KEY constraints between
-- them (e.g. product.shop_id, cart_items.product_id, orders.customer_id,
-- payment.order_id are all plain BIGINT columns, not JPA @ManyToOne
-- relations) — this matches the actual entity design exactly, it is not
-- an omission.

CREATE TABLE role (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6)  NOT NULL,
  updated_at DATETIME(6)  NOT NULL,
  role_name  VARCHAR(20)  NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  created_at    DATETIME(6)  NOT NULL,
  updated_at    DATETIME(6)  NOT NULL,
  email         VARCHAR(150) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  full_name     VARCHAR(150) NOT NULL,
  phone_number  VARCHAR(20),
  status        VARCHAR(20)  NOT NULL,
  role_id       BIGINT       NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE shop (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  created_at       DATETIME(6)  NOT NULL,
  updated_at       DATETIME(6)  NOT NULL,
  owner_id         BIGINT       NOT NULL,
  shop_name        VARCHAR(255) NOT NULL,
  shop_address     VARCHAR(255) NOT NULL,
  shop_description VARCHAR(255),
  status           VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_shop_name (shop_name),
  UNIQUE KEY uk_shop_address (shop_address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product (
  id           BIGINT        NOT NULL AUTO_INCREMENT,
  created_at   DATETIME(6)   NOT NULL,
  updated_at   DATETIME(6)   NOT NULL,
  shop_id      BIGINT        NOT NULL,
  category_id  BIGINT,
  product_name VARCHAR(255)  NOT NULL,
  description  VARCHAR(255),
  price        DECIMAL(10,2) NOT NULL,
  price_unit   VARCHAR(10)   NOT NULL DEFAULT 'KG',
  price_quantity_grams INT    NOT NULL DEFAULT 1000,
  image_url    VARCHAR(255),
  is_active    TINYINT(1)    NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE inventory (
  id             BIGINT      NOT NULL AUTO_INCREMENT,
  created_at     DATETIME(6) NOT NULL,
  updated_at     DATETIME(6) NOT NULL,
  product_id     BIGINT      NOT NULL,
  stock_quantity INT         NOT NULL COMMENT 'Stored in grams',
  PRIMARY KEY (id),
  UNIQUE KEY uk_inventory_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cart_items (
  id         BIGINT      NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  user_id    BIGINT      NOT NULL,
  product_id BIGINT      NOT NULL,
  quantity   INT         NOT NULL COMMENT 'Number of sale units/packages',
  PRIMARY KEY (id),
  UNIQUE KEY uk_cart_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE orders (
  id             BIGINT        NOT NULL AUTO_INCREMENT,
  created_at     DATETIME(6)   NOT NULL,
  updated_at     DATETIME(6)   NOT NULL,
  customer_id    BIGINT        NOT NULL,
  payment_method VARCHAR(30)   NOT NULL,
  payment_status VARCHAR(30),
  status         VARCHAR(30)   NOT NULL,
  total_amount   DECIMAL(10,2) NOT NULL,
  cancel_reason  VARCHAR(255),
  version        BIGINT        NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_item (
  id                BIGINT        NOT NULL AUTO_INCREMENT,
  created_at        DATETIME(6)   NOT NULL,
  updated_at        DATETIME(6)   NOT NULL,
  order_id          BIGINT        NOT NULL,
  product_id        BIGINT        NOT NULL,
  quantity          INT           NOT NULL COMMENT 'Number of sale units/packages',
  price_at_purchase DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product_review (
  id         BIGINT      NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  order_id   BIGINT      NOT NULL,
  product_id BIGINT      NOT NULL,
  rating     INT         NOT NULL,
  comment    TEXT,
  image_url  VARCHAR(255),
  video_url  VARCHAR(255),
  PRIMARY KEY (id),
  UNIQUE KEY uk_review_order_product (order_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payment (
  id              BIGINT        NOT NULL AUTO_INCREMENT,
  created_at      DATETIME(6)   NOT NULL,
  updated_at      DATETIME(6)   NOT NULL,
  order_id        BIGINT        NOT NULL,
  gateway_txn_ref VARCHAR(100)  NOT NULL,
  transaction_code VARCHAR(255),
  amount_paid     DECIMAL(10,2),
  payment_gateway VARCHAR(255)  NOT NULL,
  payment_message VARCHAR(255),
  raw_response    TEXT,
  status          VARCHAR(255)  NOT NULL,
  paid_at         DATETIME(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_payment_gateway_txn_ref (gateway_txn_ref)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =====================================================================
-- 3) SEED DATA
-- =====================================================================

-- --- Roles (RoleSeeder normally creates these on backend startup anyway;
--     INSERT IGNORE so this is safe to run whether or not that already
--     happened) ------------------------------------------------------
INSERT IGNORE INTO role (created_at, updated_at, role_name) VALUES
  (NOW(6), NOW(6), 'CUSTOMER'),
  (NOW(6), NOW(6), 'MANAGER'),
  (NOW(6), NOW(6), 'SHIPPER'),
  (NOW(6), NOW(6), 'ADMIN');

-- --- Test users ------------------------------------------------------
-- Password for ALL of the users below is:  Test@1234
-- (already bcrypt-hashed — log in directly, no need to /register these)

-- 1 Manager: creates/edits products, manages stock.
INSERT IGNORE INTO users (created_at, updated_at, email, password_hash, full_name, phone_number, status, role_id)
VALUES (
  NOW(6), NOW(6),
  'manager@freshmart.test',
  '$2b$12$FEiFF.oA1ThdZpOsuD/SM.RebwZBbjvK12LvWfPyUY7wmhIwO0sG.',
  'Nguyen Van Quan Ly',
  '0900000001',
  'ACTIVE',
  (SELECT id FROM role WHERE role_name = 'MANAGER')
);

-- 2 Customers: browse/cart/checkout/orders.
INSERT IGNORE INTO users (created_at, updated_at, email, password_hash, full_name, phone_number, status, role_id)
VALUES (
  NOW(6), NOW(6),
  'customer1@freshmart.test',
  '$2b$12$SYjmdMuMaU4O6QwAuyIPsOvxhFOB7P1E8IGU0iIIKbQA.DIeMOwJG',
  'Tran Thi Khach Hang',
  '0900000002',
  'ACTIVE',
  (SELECT id FROM role WHERE role_name = 'CUSTOMER')
);

INSERT IGNORE INTO users (created_at, updated_at, email, password_hash, full_name, phone_number, status, role_id)
VALUES (
  NOW(6), NOW(6),
  'customer2@freshmart.test',
  '$2b$12$Gj3rfZo4Uwn4rIob366rK.fuvnujmmA8zwCkpYF4axi/rAmOZFoc6',
  'Le Van Mua Sam',
  '0900000003',
  'ACTIVE',
  (SELECT id FROM role WHERE role_name = 'CUSTOMER')
);

-- --- Shops -----------------------------------------------------------
INSERT IGNORE INTO shop (created_at, updated_at, owner_id, shop_name, shop_address, shop_description, status)
VALUES (
  NOW(6), NOW(6),
  (SELECT id FROM users WHERE email = 'manager@freshmart.test'),
  'FreshMart Main Shop',
  '123 Nguyen Trai, District 1, Ho Chi Minh City',
  'Fresh fruit shop for local and imported produce.',
  'ACTIVE'
);

-- Hibernate-created schemas may have these new NOT NULL columns without
-- database defaults. Keep the seed runnable whether the schema came from
-- section 2 above or from Hibernate ddl-auto=update.
ALTER TABLE product
  MODIFY price_unit ENUM('G','KG') NOT NULL DEFAULT 'KG',
  MODIFY price_quantity_grams INT NOT NULL DEFAULT 1000;

-- Delete only this script's catalog rows before re-seeding so repeated runs
-- do not duplicate products. Orders/cart_items/payment are not seeded here,
-- so this is intended for fresh/manual test databases.
DELETE FROM inventory
WHERE product_id IN (
  SELECT id FROM product
  WHERE product_name IN (
    'Táo Envy Mỹ', 'Cam sành Hà Giang', 'Xoài cát Hòa Lộc', 'Nho xanh không hạt',
    'Dâu tây Đà Lạt', 'Chuối già Nam Mỹ', 'Lê Hàn Quốc (ngừng bán)', 'Táo Gala New Zealand',
    'Táo Fuji Nhật Bản', 'Táo Granny Smith', 'Lê Nam Phi', 'Lê Nhật Bản',
    'Hồng giòn Hàn Quốc', 'Kiwi xanh New Zealand', 'Kiwi vàng Zespri', 'Xoài keo Campuchia',
    'Xoài tượng Bình Định', 'Bưởi da xanh Bến Tre', 'Bưởi năm roi Vĩnh Long', 'Dưa hấu Long An',
    'Dưa hấu không hạt', 'Dưa lưới ruột cam', 'Dưa gang miền Tây', 'Nho đỏ không hạt Mỹ',
    'Nho đen Autumn Crisp', 'Cherry Mỹ size 9.5', 'Cherry New Zealand', 'Việt quất tươi',
    'Phúc bồn tử đỏ', 'Dâu tằm Đà Lạt', 'Thanh long ruột đỏ', 'Thanh long ruột trắng',
    'Măng cụt Thái Lan', 'Sầu riêng Ri6 tách múi', 'Mít Thái bóc sẵn', 'Ổi lê Đài Loan',
    'Mận An Phước', 'Chôm chôm nhãn', 'Nhãn xuồng cơm vàng', 'Vải thiều Lục Ngạn'
  )
);

DELETE FROM product
WHERE product_name IN (
  'Táo Envy Mỹ', 'Cam sành Hà Giang', 'Xoài cát Hòa Lộc', 'Nho xanh không hạt',
  'Dâu tây Đà Lạt', 'Chuối già Nam Mỹ', 'Lê Hàn Quốc (ngừng bán)', 'Táo Gala New Zealand',
  'Táo Fuji Nhật Bản', 'Táo Granny Smith', 'Lê Nam Phi', 'Lê Nhật Bản',
  'Hồng giòn Hàn Quốc', 'Kiwi xanh New Zealand', 'Kiwi vàng Zespri', 'Xoài keo Campuchia',
  'Xoài tượng Bình Định', 'Bưởi da xanh Bến Tre', 'Bưởi năm roi Vĩnh Long', 'Dưa hấu Long An',
  'Dưa hấu không hạt', 'Dưa lưới ruột cam', 'Dưa gang miền Tây', 'Nho đỏ không hạt Mỹ',
  'Nho đen Autumn Crisp', 'Cherry Mỹ size 9.5', 'Cherry New Zealand', 'Việt quất tươi',
  'Phúc bồn tử đỏ', 'Dâu tằm Đà Lạt', 'Thanh long ruột đỏ', 'Thanh long ruột trắng',
  'Măng cụt Thái Lan', 'Sầu riêng Ri6 tách múi', 'Mít Thái bóc sẵn', 'Ổi lê Đài Loan',
  'Mận An Phước', 'Chôm chôm nhãn', 'Nhãn xuồng cơm vàng', 'Vải thiều Lục Ngạn'
);

-- --- Products (shop_id/category_id are just placeholder numbers — there
--     is no real Shop/Category table in this project's current scope,
--     so any value works). image_url now points at the real JPGs in
--     backend/src/main/resources/static/images/, served by Spring Boot
--     at http://localhost:8080/images/<file> (no context-path). ------
INSERT INTO product (created_at, updated_at, shop_id, category_id, product_name, description, price, image_url, is_active) VALUES
  (NOW(6), NOW(6), 1, 1, 'Táo Envy Mỹ',        'Táo nhập khẩu, giòn ngọt, size to.',      65000.00, 'http://localhost:8080/images/tao_envy.jpg',   1),
  (NOW(6), NOW(6), 1, 1, 'Cam sành Hà Giang',   'Cam sành mọng nước, ít hạt.',              35000.00, 'http://localhost:8080/images/cam_sanh.jpg',   1),
  (NOW(6), NOW(6), 1, 2, 'Xoài cát Hòa Lộc',    'Xoài chín cây, thơm ngọt đặc trưng.',       75000.00, 'http://localhost:8080/images/xoai_cat.jpg',   1),
  (NOW(6), NOW(6), 1, 2, 'Nho xanh không hạt',  'Nho nhập khẩu, giòn, không hạt.',           120000.00, 'http://localhost:8080/images/nho_xanh.jpg',   1),
  (NOW(6), NOW(6), 1, 3, 'Dâu tây Đà Lạt',      'Dâu tây tươi, size lớn, vị chua ngọt.',     90000.00, 'http://localhost:8080/images/dau_tay.jpg',    1),
  (NOW(6), NOW(6), 1, 3, 'Chuối già Nam Mỹ',    'Chuối chín vừa, ngọt tự nhiên.',            25000.00, 'http://localhost:8080/images/chuoi.jpg',      1),
  (NOW(6), NOW(6), 1, 1, 'Lê Hàn Quốc (ngừng bán)', 'Sản phẩm mẫu đã ẩn để test trạng thái ngừng bán.', 55000.00, 'https://placehold.co/400x300?text=Le+Han',      0),
  (NOW(6), NOW(6), 1, 1, 'Táo Gala New Zealand', 'Táo Gala giòn ngọt, phù hợp ăn vặt mỗi ngày.', 59000.00, 'https://loremflickr.com/600/400/gala,apple,fruit', 1),
  (NOW(6), NOW(6), 1, 1, 'Táo Fuji Nhật Bản', 'Táo Fuji thơm, vị ngọt đậm và độ giòn cao.', 85000.00, 'https://loremflickr.com/600/400/fuji,apple,fruit', 1),
  (NOW(6), NOW(6), 1, 1, 'Táo Granny Smith', 'Táo xanh chua nhẹ, giòn, hợp làm salad.', 72000.00, 'https://loremflickr.com/600/400/green,apple,fruit', 1),
  (NOW(6), NOW(6), 1, 1, 'Lê Nam Phi', 'Lê tươi nhiều nước, vị thanh mát.', 62000.00, 'https://loremflickr.com/600/400/pear,fruit', 1),
  (NOW(6), NOW(6), 1, 1, 'Lê Nhật Bản', 'Lê size lớn, thịt trắng, ngọt dịu.', 98000.00, 'https://loremflickr.com/600/400/japanese,pear,fruit', 1),
  (NOW(6), NOW(6), 1, 1, 'Hồng giòn Hàn Quốc', 'Hồng giòn ngọt, ít chát, màu cam đẹp.', 78000.00, 'https://loremflickr.com/600/400/persimmon,fruit', 1),
  (NOW(6), NOW(6), 1, 1, 'Kiwi xanh New Zealand', 'Kiwi xanh chua ngọt, giàu vitamin C.', 110000.00, 'https://loremflickr.com/600/400/kiwi,fruit', 1),
  (NOW(6), NOW(6), 1, 1, 'Kiwi vàng Zespri', 'Kiwi vàng mềm ngọt, hương thơm đặc trưng.', 135000.00, 'https://loremflickr.com/600/400/golden,kiwi,fruit', 1),
  (NOW(6), NOW(6), 1, 2, 'Xoài keo Campuchia', 'Xoài keo giòn, chua ngọt, chấm muối ớt rất hợp.', 42000.00, 'https://loremflickr.com/600/400/mango,fruit', 1),
  (NOW(6), NOW(6), 1, 2, 'Xoài tượng Bình Định', 'Xoài trái lớn, cơm dày, ăn xanh hoặc chín đều ngon.', 48000.00, 'https://loremflickr.com/600/400/green,mango,fruit', 1),
  (NOW(6), NOW(6), 1, 2, 'Bưởi da xanh Bến Tre', 'Bưởi da xanh múi hồng, ngọt thanh, ít hạt.', 68000.00, 'https://loremflickr.com/600/400/pomelo,fruit', 1),
  (NOW(6), NOW(6), 1, 2, 'Bưởi năm roi Vĩnh Long', 'Bưởi năm roi mọng nước, vị thanh mát.', 52000.00, 'https://loremflickr.com/600/400/grapefruit,fruit', 1),
  (NOW(6), NOW(6), 1, 2, 'Dưa hấu Long An', 'Dưa hấu ruột đỏ, ngọt mát, trái chắc.', 18000.00, 'https://loremflickr.com/600/400/watermelon,fruit', 1),
  (NOW(6), NOW(6), 1, 2, 'Dưa hấu không hạt', 'Dưa hấu không hạt tiện lợi, phù hợp tiệc gia đình.', 28000.00, 'https://loremflickr.com/600/400/seedless,watermelon,fruit', 1),
  (NOW(6), NOW(6), 1, 2, 'Dưa lưới ruột cam', 'Dưa lưới thơm, ruột cam ngọt đậm.', 45000.00, 'https://loremflickr.com/600/400/cantaloupe,melon,fruit', 1),
  (NOW(6), NOW(6), 1, 2, 'Dưa gang miền Tây', 'Dưa gang mềm thơm, hợp làm sinh tố.', 32000.00, 'https://loremflickr.com/600/400/melon,fruit', 1),
  (NOW(6), NOW(6), 1, 3, 'Nho đỏ không hạt Mỹ', 'Nho đỏ ngọt, vỏ mỏng, không hạt.', 125000.00, 'https://loremflickr.com/600/400/red,grapes,fruit', 1),
  (NOW(6), NOW(6), 1, 3, 'Nho đen Autumn Crisp', 'Nho đen trái lớn, giòn, ngọt sâu.', 145000.00, 'https://loremflickr.com/600/400/black,grapes,fruit', 1),
  (NOW(6), NOW(6), 1, 3, 'Cherry Mỹ size 9.5', 'Cherry Mỹ quả lớn, giòn ngọt, màu đỏ đậm.', 320000.00, 'https://loremflickr.com/600/400/cherry,fruit', 1),
  (NOW(6), NOW(6), 1, 3, 'Cherry New Zealand', 'Cherry New Zealand tươi, hậu vị ngọt thanh.', 380000.00, 'https://loremflickr.com/600/400/new-zealand,cherry,fruit', 1),
  (NOW(6), NOW(6), 1, 3, 'Việt quất tươi', 'Việt quất tươi hợp ăn kèm sữa chua và ngũ cốc.', 260000.00, 'https://loremflickr.com/600/400/blueberry,fruit', 1),
  (NOW(6), NOW(6), 1, 3, 'Phúc bồn tử đỏ', 'Phúc bồn tử chua ngọt, thơm nhẹ, dùng làm bánh.', 290000.00, 'https://loremflickr.com/600/400/raspberry,fruit', 1),
  (NOW(6), NOW(6), 1, 3, 'Dâu tằm Đà Lạt', 'Dâu tằm chín mọng, vị chua ngọt tự nhiên.', 160000.00, 'https://loremflickr.com/600/400/mulberry,fruit', 1),
  (NOW(6), NOW(6), 1, 4, 'Thanh long ruột đỏ', 'Thanh long ruột đỏ ngọt dịu, màu đẹp.', 36000.00, 'https://loremflickr.com/600/400/dragonfruit,fruit', 1),
  (NOW(6), NOW(6), 1, 4, 'Thanh long ruột trắng', 'Thanh long ruột trắng thanh mát, dễ ăn.', 28000.00, 'https://loremflickr.com/600/400/white,dragonfruit,fruit', 1),
  (NOW(6), NOW(6), 1, 4, 'Măng cụt Thái Lan', 'Măng cụt vỏ tím, múi trắng, ngọt thơm.', 95000.00, 'https://loremflickr.com/600/400/mangosteen,fruit', 1),
  (NOW(6), NOW(6), 1, 4, 'Sầu riêng Ri6 tách múi', 'Sầu riêng Ri6 cơm vàng, béo thơm, đóng hộp tiện lợi.', 210000.00, 'https://loremflickr.com/600/400/durian,fruit', 1),
  (NOW(6), NOW(6), 1, 4, 'Mít Thái bóc sẵn', 'Mít Thái múi dày, giòn ngọt, đã tách hạt.', 70000.00, 'https://loremflickr.com/600/400/jackfruit,fruit', 1),
  (NOW(6), NOW(6), 1, 4, 'Ổi lê Đài Loan', 'Ổi lê giòn, ít hạt, vị ngọt nhẹ.', 30000.00, 'https://loremflickr.com/600/400/guava,fruit', 1),
  (NOW(6), NOW(6), 1, 4, 'Mận An Phước', 'Mận đỏ mọng nước, chua ngọt, ăn lạnh rất ngon.', 42000.00, 'https://loremflickr.com/600/400/plum,fruit', 1),
  (NOW(6), NOW(6), 1, 4, 'Chôm chôm nhãn', 'Chôm chôm nhãn cơm dày, tróc hạt, ngọt.', 38000.00, 'https://loremflickr.com/600/400/rambutan,fruit', 1),
  (NOW(6), NOW(6), 1, 4, 'Nhãn xuồng cơm vàng', 'Nhãn xuồng cơm vàng dày cùi, ngọt thơm.', 65000.00, 'https://loremflickr.com/600/400/longan,fruit', 1),
  (NOW(6), NOW(6), 1, 4, 'Vải thiều Lục Ngạn', 'Vải thiều chín đỏ, cùi dày, ngọt đậm.', 58000.00, 'https://loremflickr.com/600/400/lychee,fruit', 1);

-- --- Inventory: one row per product above (must match the products'
--     insert order/ids — using product_name to look the id back up so
--     this doesn't depend on assumed auto-increment values) -----------
INSERT IGNORE INTO inventory (created_at, updated_at, product_id, stock_quantity)
SELECT NOW(6), NOW(6), id, 100000 FROM product WHERE product_name = 'Táo Envy Mỹ';
INSERT IGNORE INTO inventory (created_at, updated_at, product_id, stock_quantity)
SELECT NOW(6), NOW(6), id, 100000 FROM product WHERE product_name = 'Cam sành Hà Giang';
INSERT IGNORE INTO inventory (created_at, updated_at, product_id, stock_quantity)
SELECT NOW(6), NOW(6), id, 100000 FROM product WHERE product_name = 'Xoài cát Hòa Lộc';
INSERT IGNORE INTO inventory (created_at, updated_at, product_id, stock_quantity)
SELECT NOW(6), NOW(6), id, 100000 FROM product WHERE product_name = 'Nho xanh không hạt';
INSERT IGNORE INTO inventory (created_at, updated_at, product_id, stock_quantity)
SELECT NOW(6), NOW(6), id, 3000 FROM product WHERE product_name = 'Dâu tây Đà Lạt'; -- deliberately low, to test "hết hàng"/insufficient-stock
INSERT IGNORE INTO inventory (created_at, updated_at, product_id, stock_quantity)
SELECT NOW(6), NOW(6), id, 100000 FROM product WHERE product_name = 'Chuối già Nam Mỹ';
INSERT IGNORE INTO inventory (created_at, updated_at, product_id, stock_quantity)
SELECT NOW(6), NOW(6), id, 50000 FROM product WHERE product_name = 'Lê Hàn Quốc (ngừng bán)';

INSERT IGNORE INTO inventory (created_at, updated_at, product_id, stock_quantity)
SELECT NOW(6), NOW(6), p.id, 100000
FROM product p
WHERE NOT EXISTS (
  SELECT 1
  FROM inventory i
  WHERE i.product_id = p.id
);

-- Orders/cart_items/payment are intentionally NOT seeded here — the most
-- useful way to test those is by actually going through the app's own
-- flow (login as a seeded customer -> browse -> add to cart -> checkout)
-- so you're testing the real code path, not a hand-inserted shortcut.


-- =====================================================================
-- Quick sanity check — run this after the script to confirm everything landed
-- =====================================================================
-- SELECT (SELECT COUNT(*) FROM role) AS roles,
--        (SELECT COUNT(*) FROM users) AS users,
--        (SELECT COUNT(*) FROM shop) AS shops,
--        (SELECT COUNT(*) FROM product) AS products,
--        (SELECT COUNT(*) FROM inventory) AS inventory_rows;
