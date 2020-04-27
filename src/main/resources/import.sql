insert into company_table (name, active, address, tax_number, bank_account_number) values ('TelnetWork Kft.', true, '1141 Budapest, Arnold utca 20', '1234567-89-0', 'HU42 1177 3058 0568 9984 3058 0000');
insert into company_table (name, active, address, tax_number, bank_account_number) values ('BAV Zrt.', true, '1166 Budapest, Blog tér 8', '0987654-32-1', 'EN42 1177 3058 0568 9984 0000 9984');
insert into company_table (name, active, address, tax_number, bank_account_number) values ('TopTrade Kft.', true, '1081 Budapest, Cecil út 44', '1234567-99-0', 'GB42 1177 0568 3058 9984 0568 0000');
insert into company_table (name, active, address, tax_number, bank_account_number) values ('ELTE-Soft Kft.', true, '1131 Budapest, Dara utca 666', '0987654-66-0', 'HU42 1177 3058 0568 3058 0000 0568');
insert into company_table (name, active, address, tax_number, bank_account_number) values ('Unknown Kft.', true, '1020 Budapest, Eledel utca 1-3', '1234567-33-3', 'EU42 1177 0568 9984 9984 3058 0000');

insert into user_table (username, password, full_name, email, enabled, role, company_id, workplace_id) values ('Gabor', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', 'Kek Gabor', 'gabor@gmail.com', true, 'ROLE_ADMIN', 4, 4);
insert into user_table (username, password, full_name, email, enabled, role, company_id, workplace_id) values ('Balazs', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', 'Piros Balazs','balazs@gmail.com', true, 'ROLE_DIRECTOR', 1, 1);
insert into user_table (username, password, full_name, email, enabled, role, company_id, workplace_id) values ('Judit', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', 'Sarga Judit','judit@gmail.com', true, 'ROLE_DIRECTOR', 2, 2);
insert into user_table (username, password, full_name, email, enabled, role, company_id, workplace_id) values ('Gyuri', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', 'Zold Gyuri','gyuri@gmail.com', true, 'ROLE_DIRECTOR', 3, 3);
insert into user_table (username, password, full_name, email, enabled, role, company_id, workplace_id) values ('Emma', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', 'Lila Emma','emma@gmail.com', true, 'ROLE_MANAGER', null, 1);
insert into user_table (username, password, full_name, email, enabled, role, company_id, workplace_id) values ('TTManager', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', 'Munkas Manager','ttmaanager@gmail.com', true, 'ROLE_MANAGER', null, 3);
insert into user_table (username, password, full_name, email, enabled, role, company_id, workplace_id) values ('Old Student', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', 'Okos Hallgato','okos.hallgato@gmail.com', false, 'ROLE_MANAGER', null, 4);

--TopTrade requests a processor from TelnetWork, both managers were selected.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id, created_at, modified_at) values ('50000', 'Intel Core I5 6550 Processor', 'IN_STOCK', 3, 1, 6, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- 1
--TopTrade requests a processor from TelnetWork, both managers were selected.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id, created_at, modified_at) values ('40000', 'Intel Core I3 6550 Processor', 'UNDER_SHIPPING', 3, 1, 6, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- 2
--BAV requests a Display from TelnetWork, both managers were selected.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id, created_at, modified_at) values ('25000', 'Dell Display', 'SUCCESSFULLY_COMPLETED', 2, 1, 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- 3
--BAV requests carpet from TopTrade, seller manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id, created_at, modified_at) values ('110000', '20 square meter Carpet', 'NEW', 2, 3, 3, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- 4
--BAV gives an offer for a statue(decoration) for TelnetWork, buyer manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id, created_at, modified_at) values ('110000', 'Statue (Decoration)', 'NEW', 1, 2, NULL, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- 5
--BAV requests a Computer from TelnetWork, seller manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id, created_at, modified_at) values ('110000', 'Office Computer', 'NEW', 2, 1, 3, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- 6
--ELTE gives an offer for a software development for TelnetWork, buyer manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id, created_at, modified_at) values ('1000000', 'Development of a Warehouse management application', 'NEW', 1, 4, NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- 7

insert into history_table (history_type, note, order_id, creator_id, created_at) values ('EMAIL_SENT', 'I sent out the specifications of requested Intel Core I5 processor', 1, 5, CURRENT_TIMESTAMP);
insert into history_table (history_type, note, order_id, creator_id, created_at) values ('MADE_AN_OFFER', 'I made an offer for the Intel Core I5 processor', 1, 5, CURRENT_TIMESTAMP);
insert into history_table (history_type, note, order_id, creator_id, created_at) values ('PHONE_CALL', 'I talked with the manager, they will probably accept our offer', 1, 5, CURRENT_TIMESTAMP);
insert into history_table (history_type, note, order_id, creator_id, created_at) values ('NOTE', 'We are planning to accept the offer', 1, 6, CURRENT_TIMESTAMP);
insert into history_table (history_type, note, order_id, creator_id, created_at) values ('NOTE', 'Stuff', 1, 6, CURRENT_TIMESTAMP);

insert into history_table (history_type, note, order_id, creator_id, created_at) values ('EMAIL_SENT', 'I sent out the price and the details of the product.', 2, 5, CURRENT_TIMESTAMP);
insert into history_table (history_type, note, order_id, creator_id, created_at) values ('EMAIL_SENT', 'They Accepted your offer.', 2, 5, CURRENT_TIMESTAMP);
insert into history_table (history_type, note, order_id, creator_id, created_at) values ('STARTED_SHIPPING', 'I ordered Shipping', 2, 5, CURRENT_TIMESTAMP);
insert into history_table (history_type, note, order_id, creator_id, created_at) values ('SHIPPED', 'Success', 2, 6, CURRENT_TIMESTAMP);

insert into history_table (history_type, note, order_id, creator_id, created_at) values ('PAID', 'They paid the price of the product (Dell Display)', 3, 2, CURRENT_TIMESTAMP);
insert into history_table (history_type, note, order_id, creator_id, created_at) values ('PHONE_CALL', 'Everything went smoothly, we have delivered the product', 3, 2, CURRENT_TIMESTAMP);

insert into history_table (history_type, note, order_id, creator_id, created_at) values ('ORDER', 'I ordered the carpet for the new office, waiting for manager to respond from TopTrade Kft.', 4, 3, CURRENT_TIMESTAMP);

insert into history_table (history_type, note, order_id, creator_id, created_at) values ('ORDER', 'I gave an offer for a statue(decoration) for TelnetWork, nobody answered yet.', 5, 3, CURRENT_TIMESTAMP);

insert into history_table (history_type, note, order_id, creator_id, created_at) values ('ORDER', 'I ordered a computer to the new office, waiting for manager to response from TelnetWork Kft.', 6, 3, CURRENT_TIMESTAMP);

insert into history_table (history_type, note, order_id, creator_id, created_at) values ('OFFER', 'I gave an offer for a Warehouse management application for TelnetWork, nobody answered yet.', 7, 1, CURRENT_TIMESTAMP);

