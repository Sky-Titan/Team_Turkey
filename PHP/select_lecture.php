<?php
$con= mysqli_connect("localhost", "root", "qkrwns12","alido");

if(mysqli_connect_errno($con))
{
    echo "Failed to connect to Mysql: ".mysqli_connect_error();
}

mysqli_set_charset($con, "utf8");

$res = mysqli_query($con, "select * from lecture");
$result = array();

while($row = mysqli_fetch_array($res)){
    
    array_push($result, 
            array('number'=>$row[0],'title'=>$row[1],'professor'=>$row[2],'total'=>$row[3],'applicant'=>$row[4]));
}
echo json_encode(array("result"=>$result));
mysqli_close($con);
?>