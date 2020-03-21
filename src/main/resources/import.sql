insert into company_table (name) values ('TelnetWork Kft.');
insert into company_table (name) values ('BAV Zrt.');
insert into company_table (name) values ('TopTrade Kft.');
insert into company_table (name) values ('ELTE-Soft Kft.');
insert into company_table (name) values ('Unknown Kft.');

insert into user_table (username, password, enabled, role, company_id, workplace_id) values ('Gabor', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_ADMIN', 4, 4);
insert into user_table (username, password, enabled, role, company_id, workplace_id) values ('Balazs', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_DIRECTOR', 1, 1);
insert into user_table (username, password, enabled, role, company_id, workplace_id) values ('Judit', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_DIRECTOR', 2, 2);
insert into user_table (username, password, enabled, role, company_id, workplace_id) values ('Gyuri', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_DIRECTOR', 3, 3);
insert into user_table (username, password, enabled, role, company_id, workplace_id) values ('Emma', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_MANAGER', null, 1);
insert into user_table (username, password, enabled, role, company_id, workplace_id) values ('TTManager', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_MANAGER', null, 3);
insert into user_table (username, password, enabled, role, company_id, workplace_id) values ('Old Student', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', false, 'ROLE_MANAGER', null, 4);

--TopTrade requests a processor from TelnetWork, both managers were selected.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('50000', 'Intel Core I5 6550 Processor', 'IN_STOCK', 3, 1, 6, 5); -- 1
--TopTrade requests a processor from TelnetWork, both managers were selected.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('40000', 'Intel Core I3 6550 Processor', 'UNDER_SHIPPING', 3, 1, 6, 5); -- 2
--BAV requests a Display from TelnetWork, both managers were selected.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('25000', 'Dell Display', 'SUCCESSFULLY_COMPLETED', 2, 1, 3, 2); -- 3
--BAV requests carpet from TopTrade, seller manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('110000', '20 square meter Carpet', 'NEW', 2, 3, 3, NULL); -- 4
--BAV gives an offer for a statue(decoration) for TelnetWork, buyer manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('110000', 'Statue (Decoration)', 'NEW', 1, 2, NULL, 3); -- 5
--BAV requests a Computer from TelnetWork, seller manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('110000', 'Office Computer', 'NEW', 2, 1, 3, NULL); -- 6
--ELTE gives an offer for a software development for TelnetWork, buyer manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('1000000', 'Development of a Warehouse management application', 'NEW', 1, 4, NULL, 1); -- 7

insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('EMAIL_SENT', 'I sent out the specifications of requested Intel Core I5 processor', 1, 5, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP );
insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('MADE_AND_OFFER', 'I made an offer for the Intel Core I5 processor', 1, 5, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP );
insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('PHONE_CALL', 'I talked with the manager, they will probably accept our offer', 1, 5, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP );
insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('NOTE', 'We are planning to accept the offer', 1, 6, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP );
insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('NOTE', 'Stuff', 1, 6, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP );

insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('EMAIL_SENT', 'I sent out the price and the details of the product.', 2, 5, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP );
insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('EMAIL_SENT', 'They Accepted your offer.', 2, 5, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP);
insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('STARTED_SHIPPING', 'I ordered Shipping', 2, 5, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP );
insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('SHIPPED', 'Success', 2, 6, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP );

insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('PAID', 'They paid the price of the product (Dell Display)', 3, 2, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP );
insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('PHONE_CALL', 'Everything went smoothly, we have delivered the product', 3, 2, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP );

insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('ORDER', 'I ordered the carpet for the new office, waiting for manager to respond from TopTrade Kft.', 4, 3, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP);

insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('ORDER', 'I gave an offer for a statue(decoration) for TelnetWork, nobody answered yet.', 5, 3, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP);

insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('ORDER', 'I ordered a computer to the new office, waiting for manager to response from TelnetWork Kft.', 6, 3, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP);

insert into history_table (history_type, note, order_id, creator_id, created_at, updated_at) values ('OFFER', 'I gave an offer for a Warehouse management application for TelnetWork, nobody answered yet.', 7, 1, CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP);

