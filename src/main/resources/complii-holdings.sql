SELECT
       'GBST' as "Licensee",
       clcode AS "AccountNumber",
       seccode as "Security",
       "Holdings"."market-id" as "Market",
       current_date as "HoldingsDate",
       "Sponsored Holdings2" + "Nominee Holdings2" as "SponsoredVolume",
       "Portfolio Holdings" - ("Sponsored Holdings2" + "Nominee Holdings2") as "UnsponsoredVolume",
	row_modified

from (

    SELECT ch.seccode,
           ch.clcode,
		   ch.secid,
		   ch."market-id",
           (    SELECT COALESCE(SUM("Portfolio Holdings"), 0) AS "Portfolio Holdings"
                FROM (
                    SELECT clihold."total-hold" AS "Portfolio Holdings"
                    FROM shares."cli-hold" AS clihold
                    WHERE clihold.clcode = ch.clcode
                      AND clihold."market-id" = ch."market-id"
                      AND clihold.secid = cs.secid
                      AND clihold."total-hold" <> 0
                ) AS "Portfolio" ) AS "Portfolio Holdings",

            (   SELECT COALESCE(SUM("Sponsored Holdings2"), 0) AS "Sponsored Holdings2"
                FROM (

                    SELECT pschold."psc-hold" AS "Sponsored Holdings2"
                    FROM shares."psc-hold" AS pschold
                    WHERE pschold.clcode = ch.clcode
                      AND pschold.secid = cs.secid
                      AND pschold."psc-hold" <> 0

                ) AS "Sponsored2" ) AS "Sponsored Holdings2",


        (   SELECT COALESCE(SUM("Nominee Holdings2"), 0) AS "Nominee Holdings2"
            FROM (
                SELECT cnihold."cni-hold" AS "Nominee Holdings2"
                FROM shares."cni-hold" AS cnihold
                WHERE cnihold.clcode = ch.clcode
                  AND cnihold.secid = cs.secid
                  AND cnihold."cni-hold" <> 0
            ) AS "Nominee2" ) AS "Nominee Holdings2",
	ch.row_modified as "row_modified"


    FROM shares."cli-hold" ch
    JOIN shares."coded-security" cs ON ch.secid = cs.secid AND ch.seccode = cs.seccode
    JOIN shares.client c ON c.clcode = ch.clcode
    WHERE cs."active-from" <= current_date AND (cs."active-to" IS NULL OR cs."active-to" >= current_date
    )
    and c."cl-type" = 'STAFF'

) AS "Holdings"

JOIN shares."mkt-issuer" mki ON (mki."market-id" = "Holdings"."market-id" AND mki."issuer-id" = (SELECT s."issuer-id" FROM shares."security" s WHERE s.secid = "Holdings".secid))

