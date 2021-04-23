var express = require("express");
var mysql = require("mysql");
var jwt = require('jsonwebtoken');
var bodyParser = require('body-parser');
var app = express();
var config = require('./config.js');
app = config(app);
var CryptoJS = require("crypto-js");
const { PythonShell } = require('python-shell');

app.use('/', express.static(__dirname));
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var con = mysql.createConnection({
	user: process.argv[2],
	host : "localhost",
	password: process.argv[3],
	database: process.argv[4],
	insecureAuth: true
});


con.connect(function(err) {
	  if (err) 
		console.log("err " + err);
	  console.log("Connected!");
});

app.listen(8002, () => {
	console.log(process.argv[2]);
	console.log("Server running on port 8002");
});

app.get("/AddUser", (req, res, next) => {
	if(!req.headers.authorization || req.headers.authorization.indexOf('Basic') === -1){
		return res.status(401).json({message: 'No Auth'});
	}
	console.log(req.headers.authorization);
	const base64Cred = req.headers.authorization.split(' ')[1];
	const cred = Buffer.from(base64Cred, 'base64').toString('ascii');
	const creds = cred.split(':');
	console.log(creds[0] + "    " + creds[1]);
	var sqlCheckUsername = "Select * from Login where Username = ?"
	var value = CryptoJS.MD5(creds[0]).toString();
	con.query(sql, values, function(err, result){
		if(err.code == 'ER_EMPTY_QUERY')
		{
			console.log("HERE");
		}
		else if(err.code != 'ER_EMPTY_QUERY'){console.log("HERE2");throw err;}
		else{
			return res.status(400).json({message: 'Username Already taken'})
		}
	});
		
	
	var sql = "INSERT INTO Login (Username, Password) VALUES (?,?)";
	var values = [CryptoJS.MD5(creds[0]).toString(), CryptoJS.MD5(creds[1]).toString()];
	con.query(sql, values, function(err, result){
		
		if(err) {throw err};
		console.log("Records inserted: " + result.affectedRows);
	});
	var signedToken = signToken(creds[0]);
	return res.status(200).json({message: 'good', token: signedToken});;

});       

function signToken(Username){
	return jwt.sign({
		data: Username
	}, 'testSecret', { expiresIn: '24h'});
}

app.get("/AuthUser", (req,res,next) => {
        if(!req.headers.authorization || req.headers.authorization.indexOf('Basic')
	=== -1){
	                return res.status(401).json({message: 'No Auth'});
	        }
        const base64Cred= req.headers.authorization.split(' ')[1];
        const cred = Buffer.from(base64Cred, 'base64').toString('ascii');
        const creds = cred.split(':');
	console.log(creds[0] + "   " + creds[1]);
	var sql = "SELECT Password FROM Login WHERE Username = ?";
	var values = [CryptoJS.MD5(creds[0]).toString()];
	con.query(sql, values, function(err, result){
		if(err) throw err;
		console.log(result[0].Password + " <- result / inputed Pass -> " + CryptoJS.MD5(creds[1]));
		if(result[0] == null)
			return res.status(404).json({message : "Username not valid"});
		if(result[0].Password == CryptoJS.MD5(creds[1]).toString())
		{
			console.log("Found User");
       	 		var signedToken = signToken(creds[0]);
			return res.status(200).json({message: 'good', token: signedToken});
		}
		else
			return res.status(401).json({message : "Bad"});
	});
	
});



app.post("/AddLure", (req,res,next) => {
	//verify user is authed
	if(!IsAuth(req)){
		return res.status(401).json({message : "Not Auth"});
	}
	var json = JSON.parse(JSON.stringify(req.body))
	        console.log(json)
	        console.log(typeof(json))
	        var name = json.Name;
	        console.log(name)
	        var depthdata = json.Depths;
	        console.log("Depth data -> " + depthdata)
	        var d = 0;
	        var depthArr = [depthdata.length]
	        var lineoutArr = [depthdata.length];
	        for(var i = 0; i < depthdata.length; i++)
		{
			console.log(depthdata[i].subDepth + " " + depthdata[i].lineOut)
	                depthArr[i] = depthdata[i].subDepth;
	                lineoutArr[i] = depthdata[i].lineOut;
	        }

	        let options = {
	            	mode: 'text',
	     		//pythonPath: 'python',
	                pythonOptions: ['-u'],
                        //scriptPath: 'path',
		        args:[depthArr, lineoutArr]
                };
		if(lineoutArr.every(item => item === 0)){
                        var sql = "insert into Gear (GearType, name, Username) values (?, ?, ?)"
	                var values = [3, name, CryptoJS.MD5(GetUsername(req)).toString()];
                        con.query(sql, values, function(err, result){
        	                if(err) throw err;
	                });
                        sql = "insert into Lure(ID, DiveEquation) values((select ID from Gear where name = ?), ?);";
                        var diveEq = "y=0(x)+0";
                        values = [name, diveEq]
                        con.query(sql, values, function(err, result){
                                if(err) throw err;
                        });

			return res.status(200).json({"Message":"OK"});
		}

	        PythonShell.run('pyProcesses/makeEq.py', options, function(err, results){
	                if(err) console.log(err);
	                console.log('results: %j', results)
	                var resultList = results[0]
	                resultList = resultList.substring(1)
	                resultList = resultList.substring(0, resultList.length - 1)
	                resultArray = resultList.split(', ')
	                console.log(resultArray[0])
	                console.log(resultArray[1])
			
	                //console.log("result 2: " + resultList[1].substring(1))
	                var sql = "insert into Gear (GearType, name, Username) values (?, ?, ?)"
                        var values = [2, name, CryptoJS.MD5(GetUsername(req)).toString()];
			con.query(sql, values, function(err, result){
				if(err) throw err;
			});
			sql = "insert into Lure(ID, DiveEquation) values((select ID from Gear where name = ?), ?);";
			var diveEq = "y=" + resultArray[0] + "(x)" + "+" + resultArray[1];
			values = [name, diveEq]
			con.query(sql, values, function(err, result){
				if(err) throw err;
			});
			console.log("SUCCESS")
			return res.status(200).json({"Message":"OK"});
		});
});

app.post("/AddTrollingDevice",(req,res,next)=>{
	console.log("SHOULD BE HERE");
	if(!IsAuth(req)){
		return res.status(401).json({message : "Not Auth"});
	}
	console.log("HIT TROLLING DEVICE");
	var json = JSON.parse(JSON.stringify(req.body))
	console.log(json)
	console.log(typeof(json))
	var name = json.Name;
	console.log(name)
	var depthdata = json.Depths;
	console.log("Depth data -> " + depthdata)
	var d = 0;
	var depthArr = [depthdata.length]
	var lineoutArr = [depthdata.length];
	for(var i = 0; i < depthdata.length; i++)
	{
		console.log(depthdata[i].subDepth + " " + depthdata[i].lineOut)
		depthArr[i] = depthdata[i].subDepth;
		lineoutArr[i] = depthdata[i].lineOut;
	}
	
	let options = {
		mode: 'text',
		//pythonPath: 'python',
		pythonOptions: ['-u'],
		//scriptPath: 'path',
		args:[depthArr, lineoutArr]
	};
	PythonShell.run('pyProcesses/makeEq.py', options, function(err, results){
		if(err) console.log(err);
		console.log('results: %j', results)
		var resultList = results[0]
		resultList = resultList.substring(1)
		resultList = resultList.substring(0, resultList.length - 1)
		resultArray = resultList.split(', ')
		console.log(resultArray[0])
		console.log(resultArray[1])
		//console.log("result 2: " + resultList[1].substring(1))
		var sql = "insert into Gear (GearType, name, Username) values (?, ?, ?)"
		var values = [1, name, CryptoJS.MD5(GetUsername(req)).toString()];
		console.log(GetUsername(req));
		con.query(sql, values, function(err, result){
			if(err) throw err;
		});
		sql = "insert into Lure(ID, DiveEquation) values((select ID from Gear where name = ?), ?);";
		var diveEq = "y=" + resultArray[0] + "(x)" + "+" + resultArray[1];
		values = [name, diveEq]
		con.query(sql, values, function(err, result){
			if(err) throw err;
		});
		console.log("SUCCESS")
		return res.status(200).json({"Message":"OK"});
	});


	//console.log("NAME DATA -> " + name.Name)
});

function IsAuth(req)
{
	if(!req.headers.authorization || req.headers.authorization.indexOf('Bearer') === -1){
		return false;
	}
	const base64Cred = req.headers.authorization.split(' ')[1];
	const cred = Buffer.from(base64Cred, 'base64').toString('ascii');
	try{
		var decode = jwt.verify(cred, 'testSecret');
	}catch{
		return false;
	}
	return true;
}

function GetUsername(req)
{
	if(IsAuth){
		try{
			const base64Cred = req.headers.authorization.split(' ')[1];
			const cred = Buffer.from(base64Cred, 'base64').toString('ascii');
			console.log("IS AUTH AUTHED")
			var decoded = jwt.verify(cred, 'testSecret');
			console.log(decoded.data);
			return decoded.data;
		}catch(err){
			console.log("ERROR " + err)
			return "";
		}
		return decoded;
	}
}

app.get("/GetLures", (req, res, next) => {
	if(!IsAuth(req)){
		return res.status(401).json({message: 'No Auth'});
	}
	var sql = "select G.Name, L.DiveEquation from Lure L inner join Gear G on L.ID = G.ID inner join gearType GT on G.GearType = GT.id where (GT.Type = 'Diving lure' or GT.Type = 'Flat lure') and (G.Username = ?);";
	        var values = [CryptoJS.MD5(GetUsername(req)).toString()];
	        console.log(GetUsername(req));
	        con.query(sql, values, function(err, result){
			                if(err) throw err;
			                return res.status(200).json(JSON.stringify(result));
			        });

});

app.get("/GetHistory",(req,res,next) => {
	if(!IsAuth(req))
		return res.status(401).json({message: "NO AUTH"});
	var sql = "SELECT Depth, LureName, Date FROM History WHERE Username = ?;";
	var values = [CryptoJS.MD5(GetUsername(req)).toString()];
	con.query(sql,values,function(err,result){
		if(err) throw err;
		return res.status(200).json(JSON.stringify(result));
	});
});

app.get("/GetTrollingDevices",(req, res, next) => {
	if(!IsAuth(req))
		return res.status(401).json({message : "Not authorized"});
	var sql = "select G.Name, L.DiveEquation from Lure L inner join Gear G on L.ID = G.ID inner join gearType GT on G.GearType = GT.id where GT.Type = 'Diver/Trolling Device' and G.Username = ?;";
	var values = [CryptoJS.MD5(GetUsername(req)).toString()];
	console.log(GetUsername(req));
	con.query(sql, values, function(err, result){
		if(err) throw err;
		return res.status(200).json(JSON.stringify(result));
	});
});

//app.post("/postTest", (req, res, next) => {
//	console.log(req.body);
//	console.log(res.body)
//	res.json(["works"]);
//});


//app.post("/login", req, res, next) => {
	
//	console.log(req);
//});
