
    drop table if exists company_info;



    create table company_info (
        stock_code varchar(255) not null,
        addr varchar(255),
        addr_reg varchar(255),
        addr_work varchar(255),
        area varchar(255),
        create_date datetime,
        ename varchar(255),
        industry varchar(255),
        institutional decimal(19,2),
        listing_date datetime,
        lootchips decimal(19,2),
        market varchar(255),
        name varchar(255),
        offer_date datetime,
        pos_x float,
        pos_y float,
        pricelimit decimal(19,2),
        register_date date,
        shareholders decimal(19,2),
        sname varchar(255),
        stock_type varchar(255),
        web_site varchar(255),
        primary key (stock_code)
    );
drop table if exists new_item;
    create table new_item (
        id integer not null auto_increment,
        create_date datetime,
        keyword varchar(255),
        publish_date datetime,
        publish_date_str varchar(255),
        query varchar(255),
        source_name varchar(255),
        stock_code varchar(10),
        summary TEXT,
        target_html longtext,
        target_url varchar(255),
        title varchar(255),
        primary key (id)
    );

    create index idx_keyword on new_item (keyword);

    create index idx_query on new_item (query);

    create index idx_stock_code on new_item (stock_code);

    create index idx_target_url on new_item (target_url);
