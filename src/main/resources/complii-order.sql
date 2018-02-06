select 
'GBST' as "Licensee", 
'GBST' as "DataSource", 
o.clcode as "AccountID",
a.advcode as "AdviserCode",
o.ordnum as "OrderNumber",
o."order-date" as "DateCreated",
o."order-date" + o."order-time" * interval '1 second' as "Timestamp", 
(case when o."order-type" = true then 'B' else 'S' end) as "BuySell",
o.seccode as "Stock",
o.units as "Qty",
(case when o.units = 0 then 0 else o.considamt/o.units end) as "Price",
o."brok-amt" as "Brokerage",
o."advice-basis" as "BasisOfAdvice",
o."advice-given" as "AdviceGiven",
'Client' as "OrderType", -- The Type of Order
org.orgname as "OrderTaker", 
(case when (o."placed-by" is null OR o."placed-by" = '') then c.given || ' ' || c.surname else o."placed-by" end) as "OrderGiver", 
o.ordnum as "OriginalOrderID",
o."brok-amt"+o.taxamt as "BrokerageGST",
o."sett-cncycode" as "Currency",
'AEDT' as "Timezone", -- 	The timezone of the trade (i.e. AEST)
s.scheddescrip as "BrokerageDesc",
o.ordnum as "InternalOrderNumber",
o."order-comm" as "InternalData", 
o."market-id" as "MarketID"
from shares."order" o
join shares.organisation org on o.orgcode = org.orgcode
join shares.client c on c.clcode = o.clcode
join shares.advisor a on o.advisorid = a.advisorid
join shares.shschedule s on s.scheduleid = o.brokschedid


where c."cl-status" = 'ACTIVE'
