CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE product (
     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
     name VARCHAR(255) NOT NULL,
     description VARCHAR(2000) NOT NULL,
     price bigint NOT NULL,
     stock bigint NOT NULL
);

CREATE TABLE product_img (
     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
     product_id UUID REFERENCES product(id) ON DELETE CASCADE,
     img VARCHAR(2000) NOT NULL
);