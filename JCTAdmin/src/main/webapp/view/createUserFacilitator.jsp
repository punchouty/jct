<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="menu.sub.create.user.facilitator" /></title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    
    <link href="../css/bootstrap.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
    <link rel="stylesheet" href="../css/alertify.core.css" />
	<link rel="stylesheet" href="../css/alertify.default.css" id="toggleCSS" />
	<meta name="viewport" content="width=device-width">
	<link rel="shortcut icon" href="/admin/img/crafting_ico.ico" />
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
	<script type="text/javascript" src="../lib/jquery.js"></script>
	<script type="text/javascript" src="../lib/spine.js"></script>
	<script type="text/javascript" src="../lib/ajax.js"></script>
	<script src="http://code.jquery.com/jquery-latest.min.js"></script>
	<script src="../lib/jquery-1.7.2.min.js"></script>
	<script>
	if (typeof jQuery == 'undefined') {
    	document.write(unescape("%3Cscript src='../js/latest_10.2_jquery.js' type='text/javascript'%3E%3C/script%3E"));
    }
	</script>	
	<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js" type="text/javascript"></script>
	<script type="text/javascript">
	//<![CDATA[
        (window.jQuery && window.jQuery.ui && window.jQuery.ui.version === '1.10.3')||document.write('<script type="text/javascript" src="../js/jquery_ui.js"><\/script>');//]]>
    </script>	
	<script src="../js/alertify.min.js"></script>
	<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"> 
</head>
	<body>
	    <div class="main_warp">
	    
	    <!-- Header area start -->
	    <jsp:include page="header.jsp"/>
	    <!-- Header area end --> 	       
	        <div class="row">  
	            <div class="main-cont-wrapper" >
	            <div class="col-sm-2 pd-0">
	            	<!-- Menu area start -->
	    			<jsp:include page="sideMenuNew.jsp"/>
	   				 <!-- Menu area end -->   
	            </div>
	            
	            <div class="col-sm-10 col">
	            <div class= "heading_label">
	            	<ol class="breadcrumbAdmin">
           				<li class="first_nav"><spring:message code="reports.breadcrumb.manage.user.home"/> &nbsp;|</li>
           				<li class="first_nav"><spring:message code="menu.app.manage.user"/> &nbsp;|</li>
           				<li class="second_nav" id='updateId'><spring:message code="menu.sub.create.user.facilitator"/></li>        				          				
             		</ol>
	            </div>
	            <br>
	            <div class= "sub_contain"></div>
	            <div class= "main_contain">	 
	            <div class="tabs">
	                   <ul class="nav nav-tabs">
					    <li class="active"><a href="#tab1">Subscribe New Users</a></li>
					    <li><a href="#tab2">Purchase More Subscriptions</a></li>
					  </ul> 
	            <!--  <div class="form_area_top"> -->
	            <%--  <div class= "inner_container"><spring:message code="menu.sub.create.user.list"/></div>  --%>
	            <div class="tab-content">
				<!-- Facilitator credential div start -->
					<div id="tab1" class="tab active">
	            <form class="form-horizontal main_div_facilitator" name="addNewUser" autocomplete="on" novalidate enctype="multipart/form-data" data-remote="true"> 	            
	                         
                 <!--  Subscribed User -->
				 <div class="single_form_item">
                     <div class="col-sm-4 ">
                     <label for="inputUP" class="col-sm-12 control-label control-label-renew text_label"><spring:message code="label.user.totalsubscribed"/></label></div>
                     <div class="col-sm-1">
                     	<input id="subscribedUserId" class="control-label-disable width_150 disable_style" type="text" value="" maxlength="50" disabled="true"></input>
                     </div> 
                     <div class="col-sm-1"></div>
					 <div class="col-sm-2">
					     <label for="inputUP" class="col-sm-12 control-label text_label width_120 float_right"><spring:message code="label.user.registered"/></label>			 
					 </div>
					 <div class="col-sm-1">
					     <input id="renewdedUserId" class="control-label-disable width_150 float_right disable_style registered_user_box" type="text" value="" maxlength="50" disabled="true"></input>					 
					 </div>
					 <div class="col-sm-2"></div>
                     <div class="clearfix"></div>
                </div>                
				                
              	<div class="single_form_item">
                    <div class="col-sm-4 ">
                    <label for="inputUP" class="col-sm-12 control-label text_label"><spring:message code="menu.sub.select.option"/></label></div>
                    <div class="col-sm-5 sub_text_label">
                    	<div class="col-sm-6 radio_text">
                           <input type="radio" name="preference" id="manual" value="M" onclick="toogleSelectionDiv('M')" checked = "checked"> <spring:message code="label.manual"/>
                      	</div>
                      	<div class="col-sm-6 radio_text">
                      		<input type="radio" name="preference" id="csv" value="C" onclick="toogleSelectionDiv('C')"> <spring:message code="label.csv"/>
                      	</div>
                    </div> 
                    <div class="col-sm-3">&nbsp;</div>  
                    <div class="clearfix"></div>
               	</div>  
               
               
                 <!-- No Of User -->
                  <div id="noOFuserDiv">
                   <div class="single_form_item">
                     <div class="col-sm-4 ">
                     <label for="inputQtnNo" class="col-sm-12 control-label text_label"><spring:message code="label.no.user"/></label></div>
                     <div class="col-sm-5">
                     	<select class="form-control-general" id="inputNoOFUser" onchange="populateManualEntryField()">
                     		<option class="form-control-general" value=""><spring:message code="label.select.noOfUser_drpDwn"/> </option>                    		
                     	</select>
                     </div> 
                     <div class="col-sm-3">&nbsp;</div>  
                     <div class="clearfix"></div>
                   </div>				   
                   <div class="single_form_item" style="display: none;" id="manualEntry">
                     
                   </div>
                   </div>

                   <div id="fileUploadDiv" style="display: none;">
                   	   <div class="single_form_item">
	                     		<div class="col-sm-4 ">
	                     			<label for="csvFileLbl" class="col-sm-12 control-label text_label"><spring:message code="select.csv.file"/></label>
						 		</div>
			                    <div class="col-sm-5">
			                    	<input type="file" class="filestyle new_width" data-classButton="btn choose_file" name="filename" id="filename">
			                    </div> 
			                    <div class="col-sm-3 downloadCSVFmt_Change"><a id='downloadCSVFmt' class='download_link_style' onclick="downloadCSVFmt()" style="cursor: pointer;"><img src="../img/download.png" title="Download Sample CSV" /></a></div>  
	                   			<div class="clearfix"></div>
							</div>
                   		</div>
                                 
                   
				    <div class="single_form_item">
                     <div class="col-sm-4 ">
                     <label for="inputUP" class="col-sm-12 control-label text_label"><spring:message code="label.date.expiry"/></label></div>
                     <div class="col-sm-6">
                     	<input id="expiryDateInputId" class="form-control-general-form-redefined disable_style" type="text" autofocus="" onkeypress="return alphaNumericOnly(event)" placeholder="" value="" maxlength="60" disabled="true"></input>
                     </div> 
                     <div class="col-sm-2">&nbsp;</div>  
                     <div class="clearfix"></div>
					</div>  
                                   
                                   
                 <div class="single_form_item">
				<div class="col-sm-4">
			    	<label for="inputFN" class="col-sm-12 control-label text_label"><spring:message code="label.user.select.group.optional"/></label>
				</div>
                   <div class="col-sm-5">
                   	<select class="form-control-general" id="userGroupInptId">
                   		<option class="form-control-general" selected="selected" value="2!None"><spring:message code="label.user.select.group.none"/></option>  
					</select>
                	</div> 
                <div class="col-sm-3">&nbsp;</div>  
                <div class="clearfix"></div>
                </div>
                
                <div class="single_form_item">
                <div class="col-sm-4"></div>
                 <div class="col-sm-5">
                     <a class="create_grp_link" id="createUserGroupBtnId" onclick="populateUserGrpDiv()"><spring:message code="label.link.create.new.grp"/></a>
                 </div>
                  <div class="col-sm-3">&nbsp;</div>  
                <div class="clearfix"></div>
                 </div>
                 
                <div id="createUserGroupDiv"> 
                  <div class="single_form_item">
                   <div class="col-sm-4 ">
                   <label for="inputFN" class="col-sm-12 control-label text_label"><spring:message code="label.new.grp.name.label"/></label></div>
                   <div class="col-sm-6"><input type="text" class="form-control-general-form-redefined" maxlength="50" value=""  placeholder="User Group Name" id="newUserGroupInptId" onkeypress="return alphaNumericOnly(event)" autofocus></div> 
                   <div class="col-sm-2">
                  	 	<!-- <a class="hide_grp_link" id="hideUserGroupBtnId" onclick="hideUserGrpDiv()">Hide</a> -->
                   </div>  
                   <div class="clearfix"></div>
                </div>
                </div>
                   
                
                  <div class="single_form_item">
                     <p><input type="button" class="btn_admin btn_admin-primary btn_admin-sm search_btn" value="Subscribe" id="addNewUserBtnId" onclick="saveUser()" /></p>
                   </div>	                         
                </form>
	           <!--  </div> -->	                     
	            
	           </div>
	           
	           <div id="tab2" class="tab">
	           <form name="ajaxform" id="ajaxform" class="form-horizontal main_div_facilitator" data-remote="true" method="post">
        			<input id="custType" type="hidden" name="attributes[fac_customer_type_0]" value="Facilitator" />
  					<input id="subsType" type="hidden" name="attributes[fac_subs_type_0]" value="Renew" />
  					<input id="custEmail" type="hidden" name="attributes[fac_customer_email_0]" />
  					<input id="custId" type="hidden" name="attributes[fac_customer_id_0]" />
  					<input type="hidden" id="hiddenSubsNos" name="attributes[sub_fac_nos_of_user_0]" />
  					<input type="hidden" id="prdVarient" name="id" />
  					
        			<div class="single_form_item"><br/></div>
        			<div class="single_form_item">
					<div class="col-sm-4">
				    	<label for="inputFN" class="col-sm-12 control-label text_label"><spring:message code="label.emailId"/></label>
					</div>
                    <div class="col-sm-5">
                   		<span id="customerIdReadonly" class="col-sm-12 control-label text_label"></span>
                	</div> 
	                <div class="col-sm-3">&nbsp;</div>  
	                <div class="clearfix"></div>
                </div>
                
        			<div class="single_form_item">
						<div class="col-sm-4">
					    	<label for="inputFN" class="col-sm-12 control-label text_label"><spring:message code="label.no.user"/></label>
						</div>
	                    <div class="col-sm-5">
	                   		<input type="text" id="nos_of_user" name="quantity" class="form-control-general" maxlength="5"/>
	                	</div> 
		                <div class="col-sm-3">&nbsp;</div>  
		                <div class="clearfix"></div>
                	</div>
                	<div class="single_form_item">
	                    <p>
						    <input type="button" class="btn_admin btn_admin-primary btn_admin-sm search_btn" value="Purchase" id="buySubscriptionId" onclick="buyMoreSubscription()" />
					    </p>
                    </div>	
                    <div class="single_form_item"><br/></div>
        		</form>
	           
	           </div>
	           </div>
	            </div>	
	                                     
	                          
	           <div class="existing_data_div" id="existingDataDiv">	             
	             <div id="existingList" class= "existing_list" >
				 <table width='95%'>
				 	<tr>
				 		<td align="left">
				 			Manage Existing Users
				 		</td>
				 		<td align="right">
				 			<!--  <a href='bulkActionFacilitator.jsp' class='activate_style'>Bulk Action</a> -->
				 		</td>
				 	</tr>
				 </table>
				 </div>    
				 
				 <div id="existingUsersTableId1" class="single_form_item">
				 	<div class="col-sm-2"></div>
				 	 <div class="col-sm-3 align_center">
				 	 	<a class="manage_user_link " id="createUserGroupBtnIdq" onclick='performAction("passwordId")'><spring:message code="label.resetPassword"/></a>
				 	 </div>
				 	 <div class="col-sm-3 align_center">
				 	 	<a class="manage_user_link" id="createUserGroupBtnIdq" onclick='performAction("groupId")'>Create/Assign Group</a>
				 	 </div>
				 	 <div class="col-sm-3 align_left">
				 	 	<a class="manage_user_link" id="createUserGroupBtnIdq" onclick='performAction("renewId")'>Renew Subscription</a>
				 	 </div>
				 	  <div class="col-sm-1"></div>
				 	  <div class="clearfix"></div>
				  </div> 
				  <br>
				                  
	            <div id="existingUsersTableId" class="table_div_scroll">
	           		<div align="center">
	            		<img src="../img/dataLoading.GIF" />
	            		<p> <spring:message code="label.existing.user"/></p>
	            	</div>
	            </div> 
	            
	            </div>
	            
	            <div id="nonExistingUsersTableId" class="existing_data_div" >
	           		<div align="center">
	            		<img src="../img/dataLoading.GIF" />
	            		<p> <spring:message code="label.existing.user"/></p>
	            	</div>
	            </div>                
	                                 
	            </div>	    
	             </div>        
	            
				</div>
	    	</div>
	    	<div class="loader_bg" style="display:none"></div>
	   		<div class="loader" style="display:none"><img src="../img/Processing.gif" alt="loading"></div>
	    	</div>
	     <!-- Form area end -->
	    <!-- Footer area start -->
	    <jsp:include page="footer.jsp"/>
	    <!-- Footer area end --> 	          
		
		
<script src="https://code.jquery.com/jquery.js"></script>    
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="../js/bootstrap.min.js"></script>
<script src="../js/bootstrap-filestyle.js"></script>
<script src="../js/common.js"></script>
<script src="../js/jquery.tablesorter.js"></script>
<script src="../js/createUserFacilitator.js"></script>
<script src="../js/buyMoreSubscription.js"></script>
<script src="../js/bulkActionManageUser.js"></script>
<script type="text/javascript">
$(":file").filestyle();
</script>
<!-- ALL ARE CORRECT -->
    <div class="modal fade" id="myModal" tabindex="-1" data-backdrop="static">
    <div class="modal-dialog2" >
    <div class="modal-content2">
    <div class="new_modal_header modal-header">
        &nbsp;      
    </div>
    	<div align="center">   
      		<span id="message"></span>
      	</div>
      	<div align="center">   
      		<nav class="alertify-buttons">
      			<button class="alertify-button alertify-button-ok" id="alertify-oks" onclick="saveUserFacilitatorAfterValidation()">OK</button>
      			<button class="alertify-button alertify-button-cancel" id="alertify-cancels" onclick="closeMyModel()">Cancel</button>
      		</nav>
      	</div>
      	<div class="new_modal_header modal-header">
        	&nbsp;      
    	</div>
      	<div align="center">   
      		<span id="tableData"></span>
      	</div>
    </div>
  	</div>
	</div>
	
	<!-- ERROR -->
	<div class="modal fade" id="myModal1" tabindex="-1" data-backdrop="static">
    <div class="modal-dialog2" >
    <div class="modal-content2">
    <div class="new_modal_header modal-header">
        &nbsp;      
    </div>
    	<div align="center" class="error_msg_text">   
      		<span id="message1"></span>
      	</div>
      	<div align="center" class="error_msg_text">   
      		<span id="tableData1"></span>
      	</div>
      	<div align="center">   
      		<nav class="alertify-buttons">
      			<button class="alertify-button alertify-button-ok" id="alertify-oks1" onclick="closeMyModel1()">OK</button>
      		</nav>
      	</div>
      	<br>
    </div>
  	</div>
	</div>

<div class="modal fade" id="bulkRenewModal" tabindex="-1" data-backdrop="static">
    <div class="modal-dialog_renew" >
    <div class="modal-content_renew">
    <div class="new_modal_header modal-header">
        &nbsp;      
    </div>
    	<div align="center" class="renew_user_list">   
      		<span id="renewUser"></span>
      	</div>  
      	<br>   	
      	<div align="center">   
      		<!-- <nav class="alertify-buttons">
      			<button class="alertify-button alertify-button-ok" id="alertify-oks1" onclick="closeMyModel1()">OK</button>
      		</nav> -->      		
      		<nav class="alertify-buttons">
      			<button class="alertify-button alertify-button-ok" id="alertify-oks" onclick="renewBulkUserAfterSuccess()" autofocus>OK</button>
      			<button class="alertify-button alertify-button-cancel" id="alertify-cancels" onclick="closeMyRenewModel()">Cancel</button>
      		</nav>
      	</div>
      	<br>
      	<div align="center">   
      		<span id="tableDataRenew"></span>
      	</div>
      	<br>
    </div>
  	</div>
	</div>
	
	
	
	<div class="modal fade" id="resetPasswordModal" tabindex="-1" data-backdrop="static">
    <div class="modal-dialog_renew" >
    <div class="modal-content_renew">
    <div class="new_modal_header modal-header">
        &nbsp;      
    </div>
    	<div align="center" class="renew_user_list">   
      		<span id="resetPassword"></span>
      	</div>  
      	<br>   	
      	<div align="center">   
      		<!-- <nav class="alertify-buttons">
      			<button class="alertify-button alertify-button-ok" id="alertify-oks1" onclick="closeMyModel1()">OK</button>
      		</nav> -->      		
      		<nav class="alertify-buttons">
      			<button class="alertify-button alertify-button-ok" id="alertify-oks" onclick="resetPasswordInBatch()" autofocus>OK</button>
      			<button class="alertify-button alertify-button-cancel" id="alertify-cancels" onclick="closeResetPasswordModel()">Cancel</button>
      		</nav>
      	</div>
      	<br>
      	<div align="center">   
      		<span id="tableDataResetPassword"></span>
      	</div>
      	<br>
    </div>
  	</div>
	</div>
	
	<div class="modal fade" id="assignNewGroupModal" tabindex="-1" data-backdrop="static">
    <div class="modal-dialog_renew" >
    <div class="modal-content_renew">
    <div class="new_modal_header modal-header">
        &nbsp;      
    </div>
    	<div align="center" class="renew_user_list">   
      		<span id="assignNewGroup"></span>
      	</div>  
      	<br>
      	<div align="center" class="renew_user_list">   
      		<span id="userGroupDrpDown"></span>
      		<div class="single_form_item">
				<div class="col-sm-5">
			    	<label for="inputFN" class="col-sm-12 control-label text_label_assign_grp"><spring:message code="label.user.select.group.optional"/></label>
				</div>
                   <div class="col-sm-6">
                   	<select class="form-control-general dropdown_assign_grp" id="userGroupToAssignId">
                   		<option class="form-control-general" selected="selected" value="2!None"><spring:message code="label.user.select.group.none"/></option>  
					</select>
                	</div> 
                <div class="clearfix"></div>
                </div>
                
                <div class="single_form_item">
                <div class="col-sm-4"></div>
                 <div class="col-sm-6">
                     <a class="create_grp_modal_link" id="createNewUserGroupBtnId" onclick="populateAssignToUserGrpDiv()"><spring:message code="label.link.create.new.grp"/></a>
                 </div>
                  <div class="col-sm-1">&nbsp;</div>  
                <div class="clearfix"></div>
                 </div>
                
                <div id="createAssignUserGroupDiv"> 
                  <div class="single_form_item">
                   <div class="col-sm-5 ">
                   <label for="inputFN" class="col-sm-12 control-label text_label_assign_grp"><spring:message code="label.new.grp.name.label"/></label></div>
                   <div class="col-sm-6"><input type="text" class="form-control-general-form-redefined text_box_assign_grp" maxlength="50" value=""  placeholder="User Group Name" id="newAssignUserGroupInptId" onkeypress="return alphaNumericOnly(event)" autofocus></div> 
                   <div class="clearfix"></div>
                </div>
                </div>
      	</div>
      	<br>   	
      	<div align="center">   
      		<!-- <nav class="alertify-buttons">
      			<button class="alertify-button alertify-button-ok" id="alertify-oks1" onclick="closeMyModel1()">OK</button>
      		</nav> -->      		
      		<nav class="alertify-buttons">
      			<button class="alertify-button alertify-button-ok" id="alertify-oks" onclick="assignNewGroupAfterSuccess()" autofocus>OK</button>
      			<button class="alertify-button alertify-button-cancel" id="alertify-cancels" onclick="closeAssignGroupModal()">Cancel</button>
      		</nav>
      	</div>
      	<br>
      	<div align="center">   
      		<span id="tableDataAassignNewGroup"></span>
      	</div>
      	<br>
    </div>
  	</div>
	</div>
	
</body>
</html>