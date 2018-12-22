<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.
        $number=$_POST['number'];
        $title=$_POST['title'];
        $content=$_POST['content'];
        $post_pw=$_POST['password'];

        if(empty($title)){
            $errMSG = "제목을 입력하세요.";
        }
        if(empty($title)){
            $errMSG = "제목을 입력하세요.";
        }
        else if(empty($content)){
             $errMSG = "내용을 입력하세요.";
        }
        else if(empty($post_pw)){
            $errMSG = "비밀번호를 입력하세요.";
        }

        if(!isset($errMSG)) // 이름과 나라 모두 입력이 되었다면 
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 HumanInfo 테이블에 저장합니다. 
                $stmt = $con->prepare('INSERT INTO post_2(id,number,title, content, password) VALUES(NULL,:number, :title, :content, :password)');
          
                $stmt->bindParam(':number', $number);
                $stmt->bindParam(':title', $title);
                $stmt->bindParam(':content', $content);
                $stmt->bindParam(':password', $post_pw);

                if($stmt->execute())
                {
                    $successMSG = "새로운 게시글을 추가했습니다.";
                }
                else
                {
                    $errMSG = "게시글 추가 에러";
                }

            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
        }

    }

?>


<?php 
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;

	$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
   
    if( !$android )
    {
?>
    <html>
       <body>

            <form action="<?php $_PHP_SELF ?>" method="POST">
               
                number: <input type = "text" name = "number" />
                title: <input type = "text" name = "title" />
                content: <input type = "text" name = "content" />
                password: <input type = "text" name = "password" />
                <input type = "submit" name = "submit" />
            </form>
       
       </body>
    </html>

<?php 
    }
?>