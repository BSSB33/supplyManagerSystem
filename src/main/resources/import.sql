insert into company_table (name) values ('TelnetWork Kft.');
insert into company_table (name) values ('BAV Zrt.');
insert into company_table (name) values ('TopTrade Kft.');
insert into company_table (name) values ('ELTE-Soft Kft.');

insert into user_table (username, password, enabled, role, workplace_id) values ('Gabor', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_ADMIN', 4);
insert into user_table (username, password, enabled, role, workplace_id) values ('Balazs', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_DIRECTOR', 1);
insert into user_table (username, password, enabled, role, workplace_id) values ('Judit', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_DIRECTOR', 2);
insert into user_table (username, password, enabled, role, workplace_id) values ('Gyuri', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_DIRECTOR', 3);
insert into user_table (username, password, enabled, role, workplace_id) values ('Emma', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_MANAGER', 1);
insert into user_table (username, password, enabled, role, workplace_id) values ('TTManager', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', true, 'ROLE_MANAGER', 3);
insert into user_table (username, password, enabled, role, workplace_id) values ('Old Student', '$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..', false, 'ROLE_MANAGER', 4);

update company_table set director_id = 2 where name = 'TelnetWork Kft.';
update company_table set director_id = 3 where name = 'BAV Zrt.';
update company_table set director_id = 4 where name = 'TopTrade Kft.';
update company_table set director_id = 1 where name = 'ELTE-Soft Kft.';

--TopTrade requests a processor from TelnetWork, both managers were selected.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('50000', 'Intel Core I5 6550 Processor', 'IN_STOCK', 3, 1, 6, 5); -- 1
--TopTrade requests a processor from TelnetWork, both managers were selected.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('40000', 'Intel Core I3 6550 Processor', 'UNDER_SHIPPING', 3, 1, 6, 5); -- 2
--BAV requests a Display from TelnetWork, both managers were selected.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('25000', 'Dell Display', 'SUCCESSFULLY_COMPLETED', 2, 1, 3, 5); -- 3
--BAV requests carpet from TopTrade, seller manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('110000', '20 square meter Carpet', 'NEW', 2, 3, 3, NULL); -- 4
--BAV gives an offer for a statue(decoration) for TelnetWork, buyer manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('110000', 'Statue (Decoration)', 'NEW', 1, 2, NULL, 3); -- 5
--BAV requests a Computer from TelnetWork, seller manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('110000', 'Office Computer', 'NEW', 2, 1, 3, NULL); -- 6
--ELTE gives an offer for a software development for TelnetWork, buyer manager not set yet.
insert into order_table (price, product_name, status, buyer_id, seller_id, buyer_manager_id, seller_manager_id) values ('1000000', 'Development of a Warehouse management application', 'NEW', 1, 4, NULL, 1); -- 7

insert into history_table (history_type, note, order_id) values ('EMAIL_SENT', 'I sent out the specifications of requested Intel Core I5 processor', 1);
insert into history_table (history_type, note, order_id) values ('MADE_AND_OFFER', 'I made an offer for the Intel Core I5 processor', 1);
insert into history_table (history_type, note, order_id) values ('PHONE_CALL', 'I talked with the manager, they will possibly accept our offer', 1);

insert into history_table (history_type, note, order_id) values ('EMAIL_SENT', 'I sent out the price and the details of the product.', 2);
insert into history_table (history_type, note, order_id) values ('EMAIL_SENT', 'They Accepted your offer.', 2);
insert into history_table (history_type, note, order_id) values ('SHIPPING_STARTED', 'I started Shipping', 2);

insert into history_table (history_type, note, order_id) values ('PAID', 'They paid the price of the product (Dell Display)', 3);
insert into history_table (history_type, note, order_id) values ('PHONE_CALL', 'Everything went smoothly, we delivered the product', 3);

insert into history_table (history_type, note, order_id) values ('ORDER', 'I ordered the carpet for the new office, waiting for manager to response from TopTrade Kft.', 4);

insert into history_table (history_type, note, order_id) values ('ORDER', 'I gave an offer for a statue(decoration) for TelnetWork, nobody answered yet.', 5);

insert into history_table (history_type, note, order_id) values ('ORDER', 'I ordered a computer to the new office, waiting for manager to response from TelnetWork Kft.', 6);

insert into history_table (history_type, note, order_id) values ('OFFER', 'I gave an offer for a  Warehouse management application for TelnetWork, nobody answered yet.', 7);

