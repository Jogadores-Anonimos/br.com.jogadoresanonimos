ALTER TABLE feedbacks 
ALTER COLUMN status TYPE varchar(20) USING status::varchar;