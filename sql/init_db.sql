CREATE TABLE warehouse (
      id INT NOT NULL,
      name VARCHAR(50) NOT NULL,
      address_line_1 VARCHAR(100) NOT NULL,
      address_line_2 VARCHAR(100),
      city VARCHAR(50) NOT NULL,
      state VARCHAR(50) NOT NULL,
      country VARCHAR(50) NOT NULL,
      inventory_quantity INT NOT NULL DEFAULT 0,
      PRIMARY KEY(id)
)