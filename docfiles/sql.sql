USE stock_selector;
SHOW TABLES;
DESC tab_stock_price_shangzheng_0001;

SELECT COUNT(stock_id) FROM tab_stock_price_shangzheng_0001;
SELECT COUNT(stock_id) FROM tab_stock_price_shangzheng_0002;
SELECT COUNT(stock_id) FROM tab_stock_price_shangzheng_0003;
SELECT COUNT(stock_id) FROM tab_stock_price_shenzheng_0001;
SELECT COUNT(stock_id) FROM tab_stock_price_shenzheng_0002;
SELECT COUNT(stock_id) FROM tab_stock_price_shenzheng_0003;

SELECT * FROM tab_stock_info WHERE stock_id = 1;
SELECT * FROM tab_stock_info;

CREATE TABLE tab_result
(
	stock_id BIGINT,
	date VARCHAR(10),
    f1 DECIMAL(5,4),
    forcast_max DECIMAL(18,2),
    forcast_min DECIMAL(18,2),
	PRIMARY KEY (stock_id , date),
	CONSTRAINT fk_result_stock FOREIGN KEY (stock_id) REFERENCES tab_stock_info (stock_id)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

SELECT * FROM tab_result;

SELECT * FROM tab_stock_info WHERE stock_code = '000518';
SELECT * FROM tab_stock_info WHERE stock_id = '3431';
SELECT * FROM tab_stock_info WHERE stock_name = '中国船舶';

SELECT info.stock_code , info.stock_name , result.date , result.forcast_max , result.forcast_min 
FROM tab_result result LEFT JOIN tab_stock_info info 
ON result.stock_id = info.stock_id
WHERE result.date = '2020-01-31'
ORDER BY rise_rate DESC;

SELECT stock_price_date FROM tab_stock_price_shangzheng_0001 WHERE stock_id = '1' AND stock_price_date < NOW() UNION 
SELECT stock_price_date FROM tab_stock_price_shangzheng_0002 WHERE stock_id = '1' AND stock_price_date < NOW() UNION 
SELECT stock_price_date FROM tab_stock_price_shangzheng_0003 WHERE stock_id = '1' AND stock_price_date < NOW() UNION 
SELECT stock_price_date FROM tab_stock_price_shenzheng_0001 WHERE stock_id = '1' AND stock_price_date < NOW() UNION 
SELECT stock_price_date FROM tab_stock_price_shenzheng_0002 WHERE stock_id = '1' AND stock_price_date < NOW() UNION 
SELECT  stock_price_date FROM tab_stock_price_shenzheng_0003 WHERE stock_id = '1' AND stock_price_date < NOW()
ORDER BY stock_price_date DESC LIMIT 500;

SELECT * FROM tab_stock_sort;
SELECT * FROM tab_stock_label;

SELECT info.stock_code , info.stock_name , sort.sort_name FROM 
tab_stock_info info LEFT JOIN tab_stock_label label ON info.stock_id = label.stock_id
LEFT JOIN tab_stock_sort sort ON label.sort_id = sort.sort_id
WHERE sort.sort_name LIKE '%房地产开发%';

DESC tab_stock_sort;
DESC tab_stock_label;

/*-------------------------------------------------波段分类数据初始化-------------------------------------------------*/
DELETE FROM tab_stock_label WHERE sort_id = (SELECT sort_id FROM tab_stock_sort WHERE sort_name = '波段');
DELETE FROM tab_stock_sort WHERE sort_name = '波段';
INSERT INTO tab_stock_sort (sort_name , sort_code) VALUES ('波段' , '-');
SELECT * FROM tab_stock_sort WHERE sort_name = '波段';
SELECT * FROM tab_stock_info WHERE stock_code IN ('300014' , '000661' ,  '000708' , '002001' , '002007' , '002236' , '002311' , '002475');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2258 , 1683),
(2258 , 1723),
(2258 , 1943),
(2258 , 1949),
(2258 , 2178),
(2258 , 2252),
(2258 , 2416),
(2258 , 2856);
/*-------------------------------------------------波段分类数据初始化（结束）-------------------------------------------------*/

/*-------------------------------------------------龙头板块信息初始化-------------------------------------------------*/
SELECT * FROM tab_stock_sort WHERE sort_name LIKE '%龙头';
DELETE FROM tab_stock_sort WHERE sort_name LIKE '%龙头';
/*龙头股票信息插入*/
INSERT INTO tab_stock_sort (sort_name , sort_code) VALUES 
('智能制造龙头' , '-'),
('VR&AR龙头' , '-'),
('MiniLED龙头' , '-'),
('OLED龙头' , '-'),
('光刻机（胶）龙头' , '-'),
('光伏龙头' , '-'),
('业绩预增龙头' , '-'),
('超高清视频龙头' , '-'),
('特斯拉龙头' , '-'),
('新能源汽车龙头' , '-'),
('无人机龙头' , '-'),
('6G龙头' , '-'),
('北斗导航龙头' , '-'),
('天基互联网龙头' , '-'),
('航天龙头' , '-'),
('3D感应龙头' , '-'),
('氮化镓GaN龙头' , '-'),
('WiFi龙头' , '-'),
('数字货币龙头' , '-'),
('国产软件龙头' , '-'),
('军工龙头' , '-'),
('破镜股龙头' , '-'),
('5G龙头' , '-'),
('工业大麻龙头' , '-'),
('房地产龙头' , '-'),
('小米概念股龙头' , '-'),
('华为产业链龙头' , '-'),
('苹果产业链龙头' , '-'),
('农药龙头' , '-'),
('医药龙头' , '-'),
('阿里巴巴概念股龙头' , '-'),
('指纹识别龙头' , '-'),
('人脸识别龙头' , '-'),
('折叠屏龙头' , '-'),
('区块链龙头' , '-'),
('国产芯片龙头' , '-'),
('无人驾驶龙头' , '-');


/*指纹识别*/
SELECT * FROM tab_stock_sort WHERE sort_name = '指纹识别龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('603106' , '603005');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2388 , 1033),
(2388 , 1093);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '指纹识别龙头';

/*国产芯片*/
SELECT * FROM tab_stock_sort WHERE sort_name = '国产芯片龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('603986' , '002049' , '603160');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2392 , 1991),
(2392 , 1114),
(2392 , 1423);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '国产芯片龙头';

/*区块链*/
SELECT * FROM tab_stock_sort WHERE sort_name = '区块链龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('600570' , '600208' , '300468');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2391 , 3307),
(2391 , 184),
(2391 , 483);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '区块链龙头';

/*人脸识别*/
SELECT * FROM tab_stock_sort WHERE sort_name = '人脸识别龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('002415' , '300479' , '600728');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2389 , 2356),
(2389 , 3318),
(2389 , 640);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '人脸识别龙头';


/*折叠屏*/
SELECT * FROM tab_stock_sort WHERE sort_name = '折叠屏龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('000725' , '002387' , '300097');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2390 , 1738),
(2390 , 2328),
(2390 , 2938);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '折叠屏龙头';

/*无人驾驶*/
SELECT * FROM tab_stock_sort WHERE sort_name = '无人驾驶龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('002405' , '002444' , '600718');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2393 , 2346),
(2393 , 2385),
(2393 , 630);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '无人驾驶龙头';

/*MiniLED*/
SELECT * FROM tab_stock_sort WHERE sort_name = 'MiniLED龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('600703');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2359 , 615);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = 'MiniLED龙头';

/*VR&AR*/
SELECT * FROM tab_stock_sort WHERE sort_name = 'VR&AR龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('300431' , '002273' , '002241');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2358 , 2183),
(2358 , 2214),
(2358 , 3271);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = 'VR&AR龙头';

/*OLED*/
SELECT * FROM tab_stock_sort WHERE sort_name = 'OLED龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('000725' , '000050' , '300567');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(3260 , 1481),
(2360 , 1738),
(2360 , 3402);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = 'OLED龙头';

/*光刻机（胶）*/
SELECT * FROM tab_stock_sort WHERE sort_name = '光刻机（胶）龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('300346' , '300655');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2361 , 3187),
(2361 , 3483);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '光刻机（胶）龙头';

/*光伏*/
SELECT * FROM tab_stock_sort WHERE sort_name = '光伏龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('300274' , '601012' , '002506');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2362 , 2447),
(2362 , 3115),
(2362 , 868);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '光伏龙头';

/*特斯拉*/
SELECT * FROM tab_stock_sort WHERE sort_name = '特斯拉龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('002384' , '603348' , '603305');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2365 , 2325),
(2365 , 1162);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '特斯拉龙头';

/*无人机*/
SELECT * FROM tab_stock_sort WHERE sort_name = '无人机龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('600879' , '002389' , '000697');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2367 , 1714),
(2367 , 2330),
(2367 , 790);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '无人机龙头';

/*北斗导航*/
SELECT * FROM tab_stock_sort WHERE sort_name = '北斗导航龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('300177' , '002383' , '300101');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2369 , 2324),
(2369 , 2942),
(2369 , 3018);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '北斗导航龙头';

/*天基互联网*/
SELECT * FROM tab_stock_sort WHERE sort_name = '天基互联网龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('600118' , '601698');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2370 , 105);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '天基互联网龙头';

/*航天*/
SELECT * FROM tab_stock_sort WHERE sort_name = '航天龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('600118' , '601698');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2371 , 105);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '航天龙头';

/*3D感应*/
SELECT * FROM tab_stock_sort WHERE sort_name = '3D感应龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('002273' , '002456' , '002281');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2372 , 2214),
(2372 , 2222),
(2372 , 2397);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '3D感应龙头';

/*WiFi*/
SELECT * FROM tab_stock_sort WHERE sort_name = 'WiFi龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('002023' , '002296' , '300504');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2374 , 1965),
(2374 , 2237);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '3D感应龙头';

/*数字货币*/
SELECT * FROM tab_stock_sort WHERE sort_name = '数字货币龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('300386');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2375 , 3226);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '数字货币龙头';

/*国产软件*/
SELECT * FROM tab_stock_sort WHERE sort_name = '国产软件龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('600536' , '600718' , '600588');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2376 , 454),
(2376 , 500),
(2376 , 630);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '国产软件龙头';

/*5G*/
SELECT * FROM tab_stock_sort WHERE sort_name = '5G龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('000063' , '002792' , '600487');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2379 , 1489),
(2379 , 2725),
(2379 , 411);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '5G龙头';

/*小米概念股*/
SELECT * FROM tab_stock_sort WHERE sort_name = '小米概念股龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('002913' , '002769' , '600577');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2382 , 2703),
(2382 , 2837),
(2382 , 489);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '小米概念股龙头';

/*华为产业链*/
SELECT * FROM tab_stock_sort WHERE sort_name = '华为产业链龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('002916' , '300602' , '300136');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2383 , 2839),
(2383 , 2977),
(2383 , 3435);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '华为产业链龙头';

/*苹果产业链*/
SELECT * FROM tab_stock_sort WHERE sort_name = '苹果产业链龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('002475' , '300433' , '300136');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2384 , 1416),
(2384 , 2977),
(2384 , 3273);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '苹果产业链龙头';

/*农药*/
SELECT * FROM tab_stock_sort WHERE sort_name = '农药龙头';
SELECT * FROM tab_stock_info WHERE stock_code IN ('000553' , '000525' , '002004');
INSERT INTO tab_stock_label (sort_id , stock_id) VALUES
(2385 , 1567),
(2385 , 1595),
(2385 , 1946);
SELECT info.stock_code 
FROM tab_stock_info info LEFT JOIN tab_stock_label label
ON info.stock_id = label.stock_id 
LEFT JOIN tab_stock_sort sort
ON sort.sort_id = label.sort_id
WHERE sort.sort_name = '农药龙头';
/*-------------------------------------------------龙头板块信息初始化（结束）-------------------------------------------------*/

