SELECT *
FROM warehouse;

--Insert entry

/*
INSERT INTO warehouse (key, w_name, w_supplierkey, w_capacity, w_address, w_nationkey) VALUES (
100,    --key decimal(3,0) not null
"Marisa",    --w_name char(25) not null
20,    --w_supplierkey decimal(2,0) not null
10,    --w_capacity decimal(6,2) not null
"Merced",    --w_address varchar(40) not null
1    --w_nationkey decimal(2,0) not null 
);
*/


INSERT INTO warehouse (key, w_name, w_supplierkey, w_capacity, w_address, w_nationkey) VALUES (
    100,    --key decimal(3,0) not null
    "Costco",    --w_name char(25) not null
    (
        SELECT s_suppkey    --w_supplierkey decimal(2,0) not null
        FROM supplier
        WHERE s_name = "Supplier#000000002"
    ),
    10000000000000,    --w_capacity decimal(6,2) not null
    "Merced",    --w_address varchar(40) not null
    (
        SELECT n_nationkey    --w_nationkey decimal(2,0) not null
        FROM nation
        WHERE n_name = "FRANCE"
    ) 
);

--Bullet 1
SELECT s_name
FROM supplier, warehouse
WHERE s_suppkey = w_suppkey
GROUP BY s_suppkey
ORDER BY COUNT(w_warehousekey) ASC
LIMIT 1;


--Bullet 2
SELECT MAX(w_capacity)
FROM warehouse;

--Bullet 3
SELECT w_name
FROM warehouse, nation, region
WHERE w_nationkey = n_nationkey AND n_regionkey = r_regionkey AND r_name = "EUROPE" AND w_capacity < 100;

--Bullet 4
SELECT SUM (ps_availqty) < SUM(w_capacity)
FROM partsupp, supplier, warehouse
WHERE ps_suppkey = s_suppkey AND s_suppkey = w_supplierkey AND s_name = "Supplier#000000002";

--Bullet 5
SELECT DISTINCT w_name
FROM warehouse, nation
WHERE w_nationkey = n_nationkey AND n_name = "UNITED STATES"
ORDER BY w_capacity DESC;

--Bullet 6

/*
 SELECT s_new.s_suppkey
    FROM supplier s_old, supplier s_new
    WHERE w_suppkey
*/

/*
UPDATE warehouse
SET w_supplierkey = (
    IF  w_supplierkey = (SELECT DISTINCT s_suppkey FROM supplier WHERE s_name = "Supplier#000000002")
        SELECT "Supplier#000000001";
    ELSE
        SELECT w_supplierkey;
   
);
*/

UPDATE warehouse
SET w_supplierkey = (
    SELECT s_suppkey
    FROM supplier
    WHERE s_name = "Supplier#000000002"
)
WHERE w_supplierkey = (
    SELECT s_suppkey
    FROM supplier
    WHERE s_name = "Supplier#000000001"
);



-- Check if an entry exists

SELECT COUNT (key) AS numResults
FROM warehouse, supplier, nation
WHERE w_name = "Gueorguieva" AND w_supplierkey = s_suppkey AND s_name = "Supplier#000000002" AND w_address = "Merced" AND w_capacity = 11 AND w_nationkey = n_nationkey AND n_name = "FRANCE";








