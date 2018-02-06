select "date-created" as "DateCreated",
b.brhcode as "BranchCode", 
b.brhname as "BranchName", 
a.advcode as "AdvisorCode", 
a.advname as "AdvisorName", 
c.clcode as "Account", 
c.given as "given", 
c.surname as "surname", 
c."e-mail1" as "EMail", 
(string_to_array(ad.address,';'))[1] as "Addr1",
(string_to_array(ad.address,';'))[2] as "Addr2",
(string_to_array(ad.address,';'))[3] as "Addr3",
(string_to_array(ad.address,';'))[4] as "Addr4",
(string_to_array(ad.address,';'))[5] as "Addr5",
ad.pcode as "Postcode",
c."home-phone" as "PhoneH", 
c."work-phone" as "PhoneW", 
c."mobile-phone" as "Mobile", 
r."purpose-ref" as "HIN",
r.name1 as "RName1",
r.name2 as "RName2",
r.name3 as "RName3",
r.designation as "Designation",
r.addr1 as "RAddr1",
r.addr2 as "RAddr2",
r.addr3 as "RAddr3",
r.addr4 as "RAddr4",
r.postcode as "RPostcode",
r."ISO3-country" as "RIsoCountry",
c."cl-class" as "ClientClass",
c."cl-type" as "ClientType",
c.greeting as "Greeting"



from shares.client c
join shares.branch b on c.branchid = b.branchid
join shares.advisor a on c.advisorid = a.advisorid
left join shares.address ad on c.clcode = ad.account and ad."addr-type" = 'PP' and ad."addr-acc-type" = 'C'
left join shares.registration r on r.clcode = c.clcode and r."reg-status" = 'C' and c.sponsored = true and r.purpose = 'S'

where 
c."cl-status" = 'ACTIVE'

