update stretchy_report set report_sql='select amount.AmountDue-amount.AmountPaid as AmountDue, amount.AmountPaid as AmountPaid from
(SELECT 
(IFNULL(SUM(ls.principal_amount),0) - IFNULL(SUM(ls.principal_writtenoff_derived),0)
 + IFNULL(SUM(ls.interest_amount),0) - IFNULL(SUM(ls.interest_writtenoff_derived),0) 
 - IFNULL(SUM(ls.interest_waived_derived),0)
 + IFNULL(SUM(ls.fee_charges_amount),0) - IFNULL(SUM(ls.fee_charges_writtenoff_derived),0) 
 - IFNULL(SUM(ls.fee_charges_waived_derived),0)
 + IFNULL(SUM(ls.penalty_charges_amount),0) - IFNULL(SUM(ls.penalty_charges_writtenoff_derived),0) 
 - IFNULL(SUM(ls.penalty_charges_waived_derived),0)
) AS AmountDue, 

(IFNULL(SUM(ls.principal_completed_derived),0) - IFNULL(SUM(ls.principal_writtenoff_derived),0) + IFNULL(SUM(ls.interest_completed_derived),0) - IFNULL(SUM(ls.interest_writtenoff_derived),0) 
 - IFNULL(SUM(ls.interest_waived_derived),0)
 + IFNULL(SUM(ls.fee_charges_completed_derived),0) - IFNULL(SUM(ls.fee_charges_writtenoff_derived),0) 
 - IFNULL(SUM(ls.fee_charges_waived_derived),0)
 + IFNULL(SUM(ls.penalty_charges_completed_derived),0) - IFNULL(SUM(ls.penalty_charges_writtenoff_derived),0) 
 - IFNULL(SUM(ls.penalty_charges_waived_derived),0)
) AS AmountPaid
FROM m_office of
LEFT JOIN m_client cl ON of.id = cl.office_id
LEFT JOIN m_loan ln ON cl.id = ln.client_id
LEFT JOIN m_loan_repayment_schedule ls ON ln.id = ls.loan_id
WHERE ls.duedate = DATE(NOW()) 
and ln.loan_status_id = 300
AND 
 (of.hierarchy LIKE CONCAT((
SELECT ino.hierarchy
FROM m_office ino
WHERE ino.id = ${officeId}),"%"))) as amount' where report_name = "Demand_Vs_Collection";