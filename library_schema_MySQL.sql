create database library;

use library;

-- Stores information about book genres.
create table GENRE
(genre_id numeric(2,0),
name varchar(35),
type numeric(1,0), -- 0 - Fiction, 1 - Non-Fiction
primary key (genre_id)
);

-- Stores information about books.
create table BOOK 
(title varchar(100) not null,  -- book title
genre_id numeric(2,0), -- id of the book genre
ISBN varchar(20), -- ISBN of a book
date_published date,  -- date the book was published
publisher varchar(40), -- name of the publisher
edition numeric(2,0), -- edition number
description varchar(300), -- concise description of the book 
primary key (ISBN),
foreign key (genre_id) references GENRE(genre_id) 
);

-- Stores information about authors of books.
create table AUTHOR 
(author_id numeric(3,0), -- author's identifier
first_name varchar(20), -- first name
middle_name varchar(20),  -- middle name
last_name varchar(20) not null,  -- last name
primary key (author_id) 
);

-- Stores information about books and their author(s).
create table BOOK_AUTHOR 
(ISBN varchar (20), -- International Standard Book Number
author_id numeric(3,0), -- author's identifier
primary key (author_id, ISBN), 
foreign key (author_id) references AUTHOR(author_id), 
foreign key (ISBN) references BOOK(ISBN) 
);

-- Stores information about physical copies of books.
create table COPY 
(barcode numeric(10,0), 
ISBN varchar(20) not null, -- International Standard Book Number
comment varchar(200), -- comment to the physical quality of a book copy; null denotes that everything is fine with a book copy
primary key (barcode), 
foreign key (ISBN) references BOOK(ISBN) 
);

-- Stores information about members of the library.
create table MEMBER 
(card_no numeric(5,0), -- card number
first_name varchar(20), -- first name
middle_name varchar(20),  -- middle name
last_name varchar(20) not null, -- last name
street varchar(20), -- street name
city varchar(15), -- city
state varchar(2) check (state in ('AK', 'AL', 'AR', 'AZ', 'CA', 'CO', 'CT', 'DE', 'FL', 'GA', 'HI', 'IA', 'ID', 'IL', 'IN', 'KS', 'KY', 'LA', 'MA', 'MD', 'ME', 'MI', 'MN', 'MO', 'MS', 'MT', 'NC', 'ND', 'NE', 'NH', 'NJ', 'NM', 'NV', 'NY', 'OH', 'OK', 'OR', 'PA', 'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VA', 'VT', 'WA', 'WI', 'WV', 'WY')), -- IPS code to identify U.S. state
apt_no numeric(5,0), -- apartment number
zip_code numeric(5,0), -- zip code
phone_no numeric(12,0) not null, -- phone number
email_address varchar(20), -- email address
card_exp_date date not null, -- card expiration date
primary key (card_no) 
);

-- Stores information about loans.
create table BORROW 
(card_no numeric(5,0), -- card number of a member
barcode numeric(10,0),
date_borrowed datetime, 
date_returned datetime, 
renewals_no numeric(2,0) not null check (renewals_no >= 0) default 0, 
paid boolean,
primary key (card_no, barcode, date_borrowed),
foreign key (barcode) references COPY(barcode), 
foreign key (card_no) references MEMBER(card_no)
);