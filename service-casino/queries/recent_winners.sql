/*
------------- LITHIUM GAMES DB ------------------
*/

SELECT CONCAT('(''', name,''',''',value,'''),') AS name
	FROM
		(SELECT DISTINCT g.name, v.value
		FROM lithium_games.game g
			INNER JOIN lithium_games.game_label_value lv ON g.id = lv.game_id
			LEFT JOIN lithium_games.label_value v ON lv.label_value_id = v.id
		WHERE v.label_id = 2 AND g.name NOT LIKE '%''%'
		) b

/*
------------- PAN DB (144.217.16.54) ------------
*/

IF OBJECT_ID('dbo.games_domain', 'U') IS NOT NULL 
	DROP TABLE dbo.games_domain; 
GO

CREATE TABLE games_domain
(
	id INT IDENTITY(1,1) NOT NULL,
	name VARCHAR(MAX),
	type VARCHAR(MAX)
)
GO

INSERT INTO games_domain (name,type)
VALUES  /*[PASTE LITHIUM GAMES RESULTS] remember to remove last comma*/

/*
------------------ PAN DB (new query window) -----------------
*/


DECLARE @sql VARCHAR(Max) = 'INSERT INTO lithium_casino.winner_augmentation (amount, game_name, user_name,domain_id)
VALUES '
DECLARE @domain_id INT = 6
DECLARE @count INT = 1
DECLARE @game_name VARCHAR(MAX)
DECLARE @game_type VARCHAR(MAX)
DECLARE @amount INT
DECLARE @user_name VARCHAR(15)
DECLARE @user_two VARCHAR(15)
DECLARE @domain VARCHAR(32)
DECLARE @id INT
DECLARE @sbtr INT
DECLARE @int INT
DECLARE @num VARCHAR(15)
DECLARE @full VARCHAR(15) 
SET @domain = 'megavegas' 

WHILE @count <= 10
BEGIN
	SET @id = (SELECT TOP 1 id FROM games_domain ORDER BY NEWID())
	SELECT @game_name = name, @game_type = type FROM games_domain WHERE ID = @id
	SET @user_name = (SELECT TOP 1 user_name FROM customer ORDER BY NEWID())
	SET @user_two = (SELECT TOP 1 user_name FROM customer ORDER BY NEWID())
	SET @full = ''
	-- Remove all numbers from the 1st string
	WHILE (PATINDEX('%[0-9]%', @user_name) <> 0)
	BEGIN
		SET @int = PATINDEX('%[0-9]%', @user_name)
		SET @num = (SELECT SUBSTRING(@user_name, @int,1))
		SET @user_name = STUFF(@user_name,PATINDEX('%[0-9]%',@user_name),1,'')
		SET @full = @full +@num
	END
	-- Make sure that the second string is longer than 5 characters
	WHILE (LEN(@user_two) < 5)
	BEGIN
		SET @user_two = (SELECT TOP 1 user_name FROM customer ORDER BY NEWID())
	END
	-- Select a random number of characters to substring from the second string and combine it with the 1st string and add the numbers removed from the 1st string
	SET @sbtr = (SELECT 1 + CONVERT(INT, (5-1+1)*RAND())) 
	SET @user_name = @user_name + SUBSTRING(@user_two,1, @sbtr) +@full
	
	IF (@game_type like '%slot%')
	BEGIN
		-- Win between 10 cents and $100
		SET @amount = (SELECT 10 + CONVERT(INT, (10000-10+1)*RAND())) 
		SET @sql = @sql + '('+CAST(@amount AS VARCHAR)+' , '''+@game_name+''', '''+@domain+'/'+@user_name+''','+CAST(@domain_id AS VARCHAR)+'),'
	END
	ELSE IF (@game_type like '%table%')
	BEGIN
		-- Win between $1 and $100
		SET @amount = (SELECT 1 + CONVERT(INT, (100-1+1)*RAND())) 
		SET @sql = @sql + '('+CAST(@amount AS VARCHAR)+'00 , '''+@game_name+''', '''+@domain+'/'+@user_name+''','+CAST(@domain_id AS VARCHAR)+'),'
	END
	ELSE
	BEGIN
		-- Win between $1 And $100
		SET @amount = (SELECT 10 + CONVERT(INT, (1000-10+1)*RAND())) 
		SET @sql = @sql + '('+CAST(@amount AS VARCHAR)+'0 , '''+@game_name+''', '''+@domain+'/'+@user_name+''','+CAST(@domain_id AS VARCHAR)+'),'
	END
	SET @count = @count + 1
END
SELECT @sql


/*
Remember to drop the table when done.

IF OBJECT_ID('dbo.games_domain', 'U') IS NOT NULL 
	DROP TABLE dbo.games_domain; 
GO
*/

/*
---------------------- LITHIUM CASINO DB ----------------------
*/

-- Run results from PAN DB (new query window). remember to remove the last comma