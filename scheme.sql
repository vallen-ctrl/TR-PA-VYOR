CREATE TABLE IF NOT EXISTS `customers` (
	`id` INT AUTO_INCREMENT,
	`kode` VARCHAR(10),
	`nama` VARCHAR(100) NOT NULL,
	`tanggal_lahir` DATE NOT NULL,
	`nomor_hp` VARCHAR(20) NOT NULL,
	`tanggal_daftar` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`dihapus` SMALLINT NOT NULL DEFAULT 0,
	PRIMARY KEY(`id`),
	CONSTRAINT `uq_customer_hp` UNIQUE (`nomor_hp`)
);


CREATE TABLE IF NOT EXISTS `vouchers` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`customer_id` INT NOT NULL,
	`kode` VARCHAR(5) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
	`jenis` VARCHAR(255) NOT NULL DEFAULT 'ulang_tahun',
	`persen_diskon` DECIMAL(5,2) NOT NULL DEFAULT 10.00,
	`berlaku_dari` DATE NOT NULL,
	`berlaku_sampai` DATE NOT NULL,
	`status` ENUM("aktif", "dipakai", "kedaluwarsa") NOT NULL DEFAULT 'aktif',
	PRIMARY KEY(`id`)
);


CREATE TABLE IF NOT EXISTS `suppliers` (
	`id` INT AUTO_INCREMENT,
	`nama` VARCHAR(150) NOT NULL,
	`alamat` VARCHAR(255) NOT NULL,
	`nomor_hp` VARCHAR(20) NOT NULL,
	`dihapus` SMALLINT NOT NULL DEFAULT 0,
	PRIMARY KEY(`id`),
	CONSTRAINT `uq_supplier_nomor` UNIQUE (`nomor_hp`)
);


CREATE TABLE IF NOT EXISTS `categories` (
	`id` INT AUTO_INCREMENT,
	`nama` VARCHAR(50) NOT NULL UNIQUE,
	`dihapus` SMALLINT NOT NULL DEFAULT 0,
	PRIMARY KEY(`id`)
);


CREATE TABLE IF NOT EXISTS `products` (
	`id` INT AUTO_INCREMENT,
	`nomor` VARCHAR(13) NOT NULL,
	`nama` VARCHAR(150) NOT NULL,
	`category_id` INT NOT NULL,
	`supplier_id` INT NOT NULL,
	`satuan` VARCHAR(20) NOT NULL DEFAULT 'pcs',
	`harga_beli` DECIMAL(12,2) NOT NULL DEFAULT 0,
	`harga_jual` DECIMAL(12,2) NOT NULL DEFAULT 0,
	`stok` INT NOT NULL DEFAULT 0,
	`terakhir_disuplai` DATE NOT NULL,
	`dihapus` SMALLINT NOT NULL DEFAULT 0,
	PRIMARY KEY(`id`),
	CONSTRAINT `uq_product_nomor` UNIQUE (`nomor`)
);


CREATE TABLE IF NOT EXISTS `product_batches` (
	`id` INT AUTO_INCREMENT,
	`product_id` INT NOT NULL,
	`supplier_id` INT NOT NULL,
	`harga_beli` DECIMAL(12,2) NOT NULL,
	`qty` INT NOT NULL,
	`qty_sisa` INT NOT NULL,
	`tanggal_pemesanan` DATE NOT NULL,
	`tanggal_masuk` DATE NOT NULL,
	`tanggal_kadaluarsa` DATE NOT NULL,
	PRIMARY KEY(`id`)
);


CREATE TABLE IF NOT EXISTS `transactions` (
	`id` BIGINT AUTO_INCREMENT,
	`kode` VARCHAR(20),
	`waktu` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`customer_id` INT,
	`voucher_id` INT,
	`sub_total` DECIMAL(14,2) NOT NULL DEFAULT 0,
	`diskon_voucher` DECIMAL(14,2) NOT NULL DEFAULT 0,
	`pajak_persen` DECIMAL(5,2) NOT NULL DEFAULT 11.00,
	`pajak` DECIMAL(14,2) NOT NULL DEFAULT 0,
	`grand_total` DECIMAL(14,2) NOT NULL DEFAULT 0,
	`bayar` DECIMAL(14,2) NOT NULL DEFAULT 0,
	`kembalian` DECIMAL(14,2) NOT NULL DEFAULT 0,
	`status` ENUM("selesai", "dibatalkan", "refund") NOT NULL DEFAULT 'selesai',
	PRIMARY KEY(`id`)
);


CREATE TABLE IF NOT EXISTS `transaction_items` (
	`id` BIGINT AUTO_INCREMENT,
	`transaction_id` BIGINT NOT NULL,
	`product_id` INT NOT NULL,
	`kode` VARCHAR(13) NOT NULL,
	`nama` VARCHAR(150) NOT NULL,
	`jumlah` INT NOT NULL,
	`satuan` VARCHAR(20) NOT NULL,
	`harga` DECIMAL(12,2) NOT NULL,
	`total` DECIMAL(14,2),
	PRIMARY KEY(`id`)
);


CREATE TABLE IF NOT EXISTS `returns` (
	`id` BIGINT AUTO_INCREMENT,
	`transaction_id` BIGINT NOT NULL,
	`waktu` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`alasan` VARCHAR(255) NOT NULL,
	`total_refund` DECIMAL(14,2) NOT NULL DEFAULT 0,
	PRIMARY KEY(`id`)
);


CREATE TABLE IF NOT EXISTS `return_items` (
	`id` BIGINT AUTO_INCREMENT,
	`return_id` BIGINT NOT NULL,
	`transaction_item_id` BIGINT NOT NULL,
	`jumlah` INT NOT NULL,
	PRIMARY KEY(`id`)
);


ALTER TABLE `vouchers`
ADD FOREIGN KEY(`customer_id`) REFERENCES `customers`(`id`)
ON UPDATE NO ACTION ON DELETE RESTRICT;
ALTER TABLE `products`
ADD FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`)
ON UPDATE NO ACTION ON DELETE RESTRICT;
ALTER TABLE `products`
ADD FOREIGN KEY(`supplier_id`) REFERENCES `suppliers`(`id`)
ON UPDATE NO ACTION ON DELETE RESTRICT;
ALTER TABLE `product_batches`
ADD FOREIGN KEY(`product_id`) REFERENCES `products`(`id`)
ON UPDATE NO ACTION ON DELETE RESTRICT;
ALTER TABLE `product_batches`
ADD FOREIGN KEY(`supplier_id`) REFERENCES `suppliers`(`id`)
ON UPDATE NO ACTION ON DELETE RESTRICT;
ALTER TABLE `transactions`
ADD FOREIGN KEY(`customer_id`) REFERENCES `customers`(`id`)
ON UPDATE NO ACTION ON DELETE RESTRICT;
ALTER TABLE `transactions`
ADD FOREIGN KEY(`voucher_id`) REFERENCES `vouchers`(`id`)
ON UPDATE NO ACTION ON DELETE SET NULL;
ALTER TABLE `transaction_items`
ADD FOREIGN KEY(`transaction_id`) REFERENCES `transactions`(`id`)
ON UPDATE NO ACTION ON DELETE RESTRICT;
ALTER TABLE `transaction_items`
ADD FOREIGN KEY(`product_id`) REFERENCES `products`(`id`)
ON UPDATE NO ACTION ON DELETE RESTRICT;
ALTER TABLE `returns`
ADD FOREIGN KEY(`transaction_id`) REFERENCES `transactions`(`id`)
ON UPDATE NO ACTION ON DELETE RESTRICT;
ALTER TABLE `return_items`
ADD FOREIGN KEY(`return_id`) REFERENCES `returns`(`id`)
ON UPDATE NO ACTION ON DELETE RESTRICT;
ALTER TABLE `return_items`
ADD FOREIGN KEY(`transaction_item_id`) REFERENCES `transaction_items`(`id`)
ON UPDATE NO ACTION ON DELETE RESTRICT;


ALTER TABLE product_batches
ADD COLUMN IF NOT EXISTS ditangani BOOLEAN NOT NULL DEFAULT FALSE;
