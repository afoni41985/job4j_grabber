CREATE TABLE company (
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person (
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

insert into company(id,name) values (1,'abba');
insert into company(id,name) values (2,'beatles');
insert into company(id,name) values (3,'zippo');
insert into company(id,name) values (4,'bugaga');
insert into company(id,name) values (5,'volvo');

insert into person(id, name, company_id) values (1, 'ivan',5);
insert into person(id, name, company_id) values (2, 'petr',5);
insert into person(id, name, company_id) values (3, 'lena',4);
insert into person(id, name, company_id) values (4, 'dima',4);
insert into person(id, name, company_id) values (5, 'maksim',3);
insert into person(id, name, company_id) values (6, 'oleg',2);
insert into person(id, name, company_id) values (7, 'denis',2);
insert into person(id, name, company_id) values (8, 'galy',4);
insert into person(id, name, company_id) values (9, 'oly',2);
insert into person(id, name, company_id) values (10, 'dasha',1);
insert into person(id, name, company_id) values (11, 'pavlin',1);

select p.name, c.name from person p
join company c
on p.company_id = c.id where c.id != 5;


select c.name, count(*) persons
from person p join company c on p.company_id = c.id group by c.name
having count(*) = (select max(persons)
from (select count(*) persons
from person p join company c on p.company_id = c.id
group by c)t1);