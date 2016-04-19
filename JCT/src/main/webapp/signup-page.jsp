<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="label.signup.title"/> </title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
     <link href="css/commonStyle.css" rel="stylesheet">
	<link href="css/less-768.css" rel="stylesheet">
    <link href="css/less-1280.css" rel="stylesheet"> 
    <link href="css/datepicker.css" rel="stylesheet">
    <link href="css/bootstrap-multiselect.css" rel="stylesheet">
    <link rel="stylesheet" href="css/alertify.core.css" />
	<link rel="stylesheet" href="css/alertify.default.css" id="toggleCSS" />
	<link rel="shortcut icon" href="/user/img/crafting_ico.ico" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
	<script type="text/javascript" src="lib/jquery.js"></script>
	<script type="text/javascript" src="lib/spine.js"></script>
	<script type="text/javascript" src="lib/ajax.js"></script>
	<script src="js/alertify.min.js"></script>
 
    </head>
<body onload="changeHashOnLoad(), adjustLogoImage();">

    <div class="main_warp">

      <!-- Header area start -->
         <jsp:include page="view/loginHeader.jsp"/> 
       <!-- Header area end -->

        <!-- Form area start -->
        
        <div class="row-fluid">          
           <div class="main-cont-wrapper" id="header-wrap">
           	<div class="col-sm-12">
           			<div class="welcome_title"><spring:message code="label.job.title" /></div>
           		</div>
             <div class="form_area login-form-area">
               <div class="form_area_top header_color">
                  <div class="col-md-4 col-xs-4">
                    <p class="sign_up"><img src="img/Add-Male-User.png" class="login-img"><span>&nbsp;&nbsp;<spring:message code="label.logIn" /></span></p>
                  </div>
                  <div class="col-md-8 col-xs-12 top_signin_area">
                  
                  <div class="clearfix"></div>                    
                  </div>
                  <div class="clearfix"></div>
               </div>
               <div class="form_area_middle">
                 <div class="form_area_wraper_justified_page">
                   <div class="single_form_item">
                     <div class="col-md-4 ">
                      <%-- <label for="inputFN" class="col-md-12 control-label align-right"><spring:message code="label.firstName"/></label></div> --%>
                     <label for="inputFN" class="col-md-12 control-label align-right login-cstm-text"><spring:message code="label.userName" /></label></div>
                     <div class="col-md-5"><input type="text" class="form-control-general input-sm" maxlength="30" placeholder="Email ID" id="inputEmails" onkeypress="searchKeyPress(event);"></div> 
                     <div class="col-md-3">&nbsp;</div>  
                     <div class="clearfix"></div>
                   </div>
                   <div class="single_form_item">
                     <div class="col-md-4 ">
                     <label for="inputFN" class="col-md-12 control-label align-right login-cstm-text"><spring:message code="label.password" /></label></div>
                     <div class="col-md-5"><input type="password" class="form-control-general input-sm" maxlength="30" placeholder="Password" id="pwd" onkeypress="searchKeyPress(event);"></div> 
                     <div class="col-md-3">&nbsp;</div>  
                     <div class="clearfix"></div>
                   </div>
                   
                   </div>
                   <div class="single_form_item">
                     <p><input type="button" class="btn btn-primary btn-sm cstm-login-btn" value="Log In" id="loginBtn" /></p>
                   </div>
                   
                 <!--SOF added for lightbox link -->
                  <div class="single_form_item forgot-pass-link">
                     <div class="col-md-4 ">&nbsp;</div>
                     <div class="col-md-8 password_area">
                     <div class="col-md-12"><a href="#" data-toggle="modal" data-target="#myModal-frgtpass"><spring:message code="label.forgotPassword" /></a></div>
                     <div class="col-md-12"><a href="http://tool.jobcrafting.com/admin/">Facilitator Login</a></div>
                     <div class="clearfix"></div>
                     </div> 
                     <div class="col-md-3">&nbsp;</div>  
                     <div class="clearfix"></div>
                   </div>
                 <!-- EOF for lightbox link -->   
                   
                   
                   <div class="clearfix"></div>
                 </div>
               </div>
               <div class="form_area_bottom"></div>
             </div>
           </div>
        </div>
         <!-- Form area end -->
        <!-- Footer area start -->
        <%-- <jsp:include page="view/footer.jsp"/> --%>
        	<jsp:include page="view/footerLanding.jsp"/>
        <!-- Footer area end -->
        <div class="loader_bg" style="display:none"></div>
        <div class="loader" style="display:none"><img src="img/Processing.gif" alt="loading"></div>
    <!-- </div> -->

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://code.jquery.com/jquery.js"></script>    
<script src="js/bootstrap-datepicker.js"></script>
<script src="js/bootstrap-multiselect.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="js/bootstrap.min.js"></script>
<script src="js/bootstrap-filestyle.js"></script>
<script src="js/login.js"></script>
<script src="js/jquery.placeholder.js"></script>
<script>
  		$('input[type=text], textarea').placeholder();  
  		$('#pwd').placeholder();   
  		$('#pwd').focus(
  			    function(){
  			        var pass = $('<input type="password" class="form-control-general input-sm" placeholder="Password" id="pwd" onkeypress="searchKeyPress(event);">');
  			        $(this).replaceWith(pass);
  			        pass.focus();
  			    }
  			); 			   				
</script>
<script type="text/javascript">
$(":file").filestyle();
</script>
  <script type = "text/javascript">
   function changeHashOnLoad() {
       window.location.href += "#";
       setTimeout(changeHashAgain, 50);
   }

   function changeHashAgain() 
   {          
       window.location.href += "1";
   }

   var storedHash = window.location.hash;
   window.setInterval(function () {
       if (window.location.hash != storedHash) {
           window.location.hash = storedHash;
       }
   }, 50);
   </script>
<!-- Modal -->
<div class="modal fade" id="myModal-frgtpass" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button id="closeId" type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title" id="myModalLabel"><spring:message code="label.forgotPassword" /></h4>
      </div>
      <div class="modal-body">
                   <div class="single_form_item">
                     <div class="col-md-4 enter_email_lbl">
                     <label for="inputFN" class="col-md-12 control-label align-right">Enter E-mail ID :</label></div>
                     <div class="col-md-8"><input type="text" class="form-control-general input-sm" maxlength="30" placeholder="Enter email" id="forgotEmail" onkeypress="onKeyPress(event);"></div>  
                     <div class="clearfix"></div>
                   </div>
      </div>
      <div class="modal-footer">
        <input type="submit" class="btn btn-primary btn-sm cstm-login-btn reset_pass" value="Reset Password" id="fgtMyPwd" onclick="forgotPwd()">
        <!-- <button type="button" class="btn btn-primary">Save changes</button> -->
      </div>
    </div>
  </div>
</div>
</body>
</html>