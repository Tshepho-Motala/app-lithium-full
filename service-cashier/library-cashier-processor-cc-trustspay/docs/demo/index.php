<?php
	/***********************************************************************	
		Note: 1.用来获取csid的值的 javascript 必须放在 form的下面
			  2.<input name="csid"    type="hidden" id='csid'> 的 id值必须也是 csid
	************************************************************************/
		
		//payment Method
		$paymentMethod = "Credit Card";

		//firstname
		$firstName     = "yao";
		
		//lastname
		$lastName      = "ming";

		//email
		$email         = "yaoming@gmail.com";

		//phone
		$phone         = "1388888888";

		
		//order No.
		$num = mt_rand(1,10000);
		$orderNo       = $num;
		
		//order Currency
		$orderCurrency = "USD";
		
		//order Amount
		$orderAmount   = "0.2";
		
		// Transaction Url
		$returnUrl     = "http://".$_SERVER['HTTP_HOST']; 

		//remark
		$remark        = "remark";

		//billing country
		$country       = "china";
		
		//billing state
		$state         = "guangdong";
		
		//billing city
		$city          = "shenzhen";
		
		//billing address
		$address       = "shenzhenaddress";
		
		//billing zip
		$zip           = "518001";
		
		//customer's IP
		$ip 		   = '127.0.0.1';  

		$interfaceInfo='mystore';
		
		//
		$cardNo ='4111111111111129';

		//
		$cardExpireMonth = '07';
		//
		$cardExpireYear = '2018';
		//
		$cardSecurityCode = '805';
		//
		$issuingBank = 'abc bank';

		
			 

?>

<form  method="post" name='creditcard_checkout' action='demo1.2.php'>
      <input name="orderNo"           value="<?php echo $orderNo;?>" type="hidden">
      <input name="orderCurrency"     value="<?php echo $orderCurrency;?>" type="hidden">    
      <input name="orderAmount"       value="<?php echo $orderAmount;?>" type="hidden">	
      <input name="returnUrl"         value="<?php echo $returnUrl;?>" type="hidden">
      <input name="ip"          	  value="<?php echo $ip;?>" type="hidden">
      <input name="firstName"         value="<?php echo $firstName;?>" type="hidden">
      <input name="lastName"          value="<?php echo $lastName;?>" type="hidden">
      <input name="email"             value="<?php echo $email;?>" type="hidden">
      <input name="phone"             value="<?php echo $phone;?>" type="hidden">
	  <input name="remark"            value="<?php echo $remark;?>" type="hidden">
	  <input name="paymentMethod"     value="<?php echo $paymentMethod;?>" type="hidden">
      <input name="country"           value="<?php echo $country;?>" type="hidden">
      <input name="cardNo"            value="<?php echo $cardNo;?>" type="hidden">
      <input name="state"             value="<?php echo $state;?>" type="hidden">
      <input name="city"              value="<?php echo $city;?>" type="hidden">
      <input name="address"           value="<?php echo $address;?>" type="hidden">
      <input name="cardExpireMonth"   value="<?php echo $cardExpireMonth;?>" type="hidden">
      <input name="cardExpireYear"    value="<?php echo $cardExpireYear;?>" type="hidden">
      <input name="cardSecurityCode"  value="<?php echo $cardSecurityCode;?>" type="hidden">
      <input name="zip"               value="<?php echo $zip;?>" type="hidden">
	  <input name="issuingBank"       value="<?php echo $issuingBank;?>" type="hidden">
	  <input name="csid"              type="hidden" id='csid'>
	  <input name="interfaceInfo"     value="<?php echo $interfaceInfo;?>" type="hidden">
	  <input  type='submit' value='submit' />
</form>	


<script type='text/javascript' charset='utf-8' src='https://shoppingingstore.com/pub/sps.js'></script>