CREATE DATABASE metal_workshop_db;

USE metal_workshop_db;

CREATE TABLE clients (
	client_id INT PRIMARY KEY AUTO_INCREMENT,
	full_name VARCHAR(100) NOT NULL,
	phone VARCHAR(10) NOT NULL,
	email VARCHAR(100) NOT NULL,
	address VARCHAR(255) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE suppliers (
	supplier_id INT PRIMARY KEY AUTO_INCREMENT,
	supplier_name VARCHAR(100) NOT NULL,
	contact_person VARCHAR(50),
	phone VARCHAR(10) NOT NULL,
	address VARCHAR(255) NOT NULL,
	is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE service_categories (
	category_id INT PRIMARY KEY AUTO_INCREMENT,
	category_name VARCHAR(100)UNIQUE NOT NULL,
	unit_of_measure VARCHAR(50) NOT NULL,
	labour_rate DECIMAL(12, 2) NOT NULL,
	default_wastage_pct DECIMAL(5, 2) NOT NULL DEFAULT 0,
	is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE users (
	user_id INT PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(50) NOT NULL UNIQUE,
	password_hash VARCHAR(255) NOT NULL,
	full_name VARCHAR(100) NOT NULL,
	role VARCHAR(50) NOT NULL,
	is_active BOOLEAN NOT NULL DEFAULT TRUE,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE backup_log (
	backup_id INT PRIMARY KEY AUTO_INCREMENT,
	created_by INT NOT NULL,
	backup_type VARCHAR(50) NOT NULL,
	file_path VARCHAR(500) NOT NULL,
	status VARCHAR(50) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	
	FOREIGN KEY(created_by) REFERENCES users(user_id)
);

CREATE TABLE inquiries (
	inquiry_id INT PRIMARY KEY AUTO_INCREMENT,
	client_id INT NOT NULL,
	category_id INT NOT NULL,
	inquiry_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	site_location VARCHAR(500) NOT NULL,
	description TEXT,
	site_vist_date DATE NOT NULL,
	status VARCHAR(50) NOT NULL,
	
	FOREIGN KEY(client_id) REFERENCES clients(client_id),
	FOREIGN KEY(category_id) REFERENCES service_categories(category_id)
);

CREATE TABLE estimates (
	estimate_id INT PRIMARY KEY AUTO_INCREMENT,
	inquiry_id INT NOT NULL,
	created_by INT NOT NULL,
	estimate_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	width DECIMAL(12, 2) NOT NULL,
	height DECIMAL(12, 2) NOT NULL,
	length DECIMAL(12, 2) NOT NULL,
	quantity INT NOT NULL,
	wastage_pct DECIMAL(5,2) NOT NULL DEFAULT 0,
	material_cost DECIMAL(12,2) NOT NULL DEFAULT 0,
	labour_cost DECIMAL(12, 2) NOT NULL DEFAULT 0,
	overhead_cost DECIMAL(12, 2) NOT NULL DEFAULT 0,
	total_cost DECIMAL(12, 2) NOT NULL DEFAULT 0,

	FOREIGN KEY(inquiry_id) REFERENCES inquiries(inquiry_id),
	FOREIGN KEY(created_by) REFERENCES users(user_id)
);

CREATE TABLE workers (
	worker_id INT PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL UNIQUE,
	full_name VARCHAR(100) NOT NULL,
	role_title VARCHAR(50) NOT NULL,
	phone VARCHAR(10) NOT NULL,
	daily_wage DECIMAL(12, 2) NOT NULL,
	join_date DATE NOT NULL,
	is_active BOOLEAN NOT NULL DEFAULT TRUE,
	
	FOREIGN KEY(user_id) REFERENCES users(user_id)
);

CREATE TABLE goods_received (
	grn_id INT PRIMARY KEY AUTO_INCREMENT,
	supplier_id INT NOT NULL,
	received_by INT NOT NULL,
	received_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	invoice_ref VARCHAR(50),
	total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
	
	FOREIGN KEY(supplier_id) REFERENCES suppliers(supplier_id),
	FOREIGN KEY(received_by) REFERENCES users(user_id)
);

CREATE TABLE materials (
	material_id INT PRIMARY KEY AUTO_INCREMENT,
	material_code VARCHAR(50) UNIQUE NOT NULL,
	supplier_id INT NOT NULL,
	material_name VARCHAR(100) NOT NULL,
	material_type VARCHAR(100) NOT NULL,
	size_spec VARCHAR(100) NOT NULL,
	unit_of_measure VARCHAR(30) NOT NULL,
    unit_cost DECIMAL(12,2) NOT NULL,
    quantity_on_hand DECIMAL(12,3) NOT NULL DEFAULT 0,
    reorder_level DECIMAL(12,3) NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
	
	FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

CREATE TABLE quotations (
	quotation_id INT PRIMARY KEY AUTO_INCREMENT,
	estimate_id INT NOT NULL,
	quotation_no VARCHAR(30) UNIQUE NOT NULL,
	quotation_date DATE NOT NULL,
	valid_until DATE NOT NULL,
	margin_pct DECIMAL(5, 2) NOT NULL DEFAULT 0,
	discount_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
	total_amount DECIMAL(12, 2) NOT NULL,
	advance_required DECIMAL(12, 2) NOT NULL DEFAULT 0,
	status VARCHAR(10) NOT NULL,
	
	FOREIGN KEY(estimate_id) REFERENCES estimates(estimate_id)
);

CREATE TABLE attendance (
	attendance_id INT PRIMARY KEY AUTO_INCREMENT,
	worker_id INT NOT NULL,
	attendance_date DATE NOT NULL,
	status VARCHAR(10) NOT NULL,
	overtime_hours DECIMAL(5, 2) NOT NULL DEFAULT 0,
	
	FOREIGN KEY(worker_id) REFERENCES workers(worker_id)
);

CREATE TABLE wage_payments (
	wage_payment_id INT PRIMARY KEY AUTO_INCREMENT,
	worker_id INT NOT NULL,
	period_start DATE NOT NULL,
	period_end DATE NOT NULL,
	days_worked DECIMAL(6, 2) NOT NULL,
	gross_wage DECIMAL(12, 2) NOT NULL,
	advances_deducted DECIMAL(12, 2) NOT NULL DEFAULT 0,
	net_paid DECIMAL(12, 2) NOT NULL,
	paid_date DATE NOT NULL,
	
	FOREIGN KEY(worker_id) REFERENCES workers(worker_id)
);

CREATE TABLE worker_advances (
	advance_id INT PRIMARY KEY AUTO_INCREMENT,
	worker_id INT NOT NULL,
	wage_payment_id INT NOT NULL,
	advance_date DATE NOT NULL,
	amount DECIMAL(12, 2) NOT NULL,
	reason VARCHAR(200),
	
	FOREIGN KEY(worker_id) REFERENCES workers(worker_id),
	FOREIGN KEY(wage_payment_id) REFERENCES wage_payments(wage_payment_id)
);

CREATE TABLE job_orders (
	job_id INT PRIMARY KEY AUTO_INCREMENT,
	quotation_id INT NOT NULL,
	job_no VARCHAR(30) NOT NULL UNIQUE,
	site_address VARCHAR(255) NOT NULL,
	start_date DATE NOT NULL,
	due_date DATE NOT NULL,
	completed_date DATE,
	status VARCHAR(20) NOT NULL,
	
	FOREIGN KEY(quotation_id) REFERENCES quotations(quotation_id)
);

CREATE TABLE off_cuts (
	offcut_id INT PRIMARY KEY AUTO_INCREMENT,
	material_id INT NOT NULL,
	source_job_id INT NOT NULL,
	length DECIMAL(12, 2) NOT NULL,
	rack_location VARCHAR(100) NOT NULL,
	status VARCHAR(20) NOT NULL,
	recorded_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	
	FOREIGN KEY(material_id) REFERENCES materials(material_id),
	FOREIGN KEY(source_job_id) REFERENCES job_orders(job_id)
);

CREATE TABLE payments (
	payment_id INT PRIMARY KEY AUTO_INCREMENT,
	job_id INT NOT NULL,
	received_by INT NOT NULL,
	payment_type VARCHAR(30) NOT NULL,
	payment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	amount DECIMAL(12, 2) NOT NULL,
	method VARCHAR(20) NOT NULL,
	
	FOREIGN KEY(job_id) REFERENCES job_orders(job_id),
	FOREIGN KEY(received_by) REFERENCES workers(worker_id)
);

CREATE TABLE invoices (
	invoice_id INT PRIMARY KEY AUTO_INCREMENT,
	job_id INT NOT NULL,
	invoice_no VARCHAR(30) NOT NULL UNIQUE,
	invoice_date DATE NOT NULL,
	total_amount DECIMAL(12, 2) NOT NULL,
	status VARCHAR(20) NOT NULL,
	
	FOREIGN KEY(job_id) REFERENCES job_orders(job_id)
);

CREATE TABLE job_tasks (
	task_id INT PRIMARY KEY AUTO_INCREMENT,
	job_id INT NOT NULL,
	worker_id INT NOT NULL,
	task_description TEXT NOT NULL,
	assigned_date DATE NOT NULL,
	due_date DATE NOT NULL,
	status VARCHAR(20),
	
	FOREIGN KEY(job_id) REFERENCES job_orders(job_id),
	FOREIGN KEY(worker_id) REFERENCES workers(worker_id)
);

CREATE TABLE job_materials (
	job_material_id INT PRIMARY KEY AUTO_INCREMENT,
	job_id INT NOT NULL,
	material_id INT NOT NULL,
	offcut_id INT NOT NULL,
	qty_used DECIMAL(12, 2) NOT NULL,
	unit_cost DECIMAL(12,2) NOT NULL DEFAULT 0,
    used_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	
	FOREIGN KEY(job_id) REFERENCES job_orders(job_id),
	FOREIGN KEY(material_id) REFERENCES materials(material_id),
	FOREIGN KEY(offcut_id) REFERENCES off_cuts(offcut_id)
);

CREATE TABLE estimate_items (
	estimate_item_id INT PRIMARY KEY AUTO_INCREMENT,
	estimate_id INT NOT NULL,
	material_id INT NOT NULL,
	offcut_id INT,
	required_qty DECIMAL(12,2) NOT NULL,
	unit_cost DECIMAL(12, 2) NOT NULL,
	line_cost DECIMAL(12, 2) NOT NULL,
	wastage_qty DECIMAL(12,2) NOT NULL DEFAULT 0,
	
	FOREIGN KEY(estimate_id) REFERENCES estimates(estimate_id),
	FOREIGN KEY(material_id) REFERENCES materials(material_id),
	FOREIGN KEY(offcut_id) REFERENCES off_cuts(offcut_id)
);

CREATE TABLE goods_received_items (
	grn_item_id INT PRIMARY KEY AUTO_INCREMENT,
	grn_id INT NOT NULL,
	material_id INT NOT NULL,
	quantity_received DECIMAL(12,2) NOT NULL,
	unit_cost DECIMAL(12, 2) NOT NULL,
	line_total DECIMAL(12, 2) NOT NULL,
	
	FOREIGN KEY(grn_id) REFERENCES goods_received(grn_id),
	FOREIGN KEY(material_id) REFERENCES materials(material_id)
);