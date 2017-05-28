<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

$con=mysqli_connect("localhost","jun123101","hyun2400!","jun123101"); 
if (mysqli_connect_errno($con))  
{  
   echo "Failed to connect to MySQL: " . mysqli_connect_error();  
}  
echo 111;

mysqli_set_charset($con,"utf8");  


$id=isset($_POST['id']) ? $_POST['id'] : '';  
$password=isset($_POST['password']) ? $_POST['password'] : '';  
$name=isset($_POST['name']) ? $_POST['name'] : '';  
$hobby=isset($_POST['hobby']) ? $_POST['hobby'] : '';  
$type=isset($_POST['type']) ? $_POST['type'] : '';  
$job=isset($_POST['job']) ? $_POST['job'] : '';  
$home=isset($_POST['home']) ? $_POST['home'] : '';  
$sex=isset($_POST['sex']) ? $_POST['sex'] : '';   

echo 222;
if ($id !="" and $password !="" and $name !="" and $hobby !="" and $type !="" and $job !="" and $home !="" and $sex !=""  ){   
  
    $sql="insert into solomongo(id,password, name, sex, hobby, type, job, home) values('$id','$password', '$name', '$hobby', '$type', '$job', '$home', '$sex')";  	//age, picture 아직 입력 안했어
    $result=mysqli_query($con,$sql);  

    echo 333;

    if($result){  
       echo 'success';  
    }  
    else{  
       echo $sql;
       echo "<br />";
       echo 'failure';  
    }  
}  
  
mysqli_close($con);  
?> 
