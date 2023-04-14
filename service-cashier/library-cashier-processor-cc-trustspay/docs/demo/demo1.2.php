 <?php

 /***********************************************************************
       Description：1、Please transafer interface parameters to our payment gateway by using POST.
                    2、Testing datas,Signkey,MerchantNo.,GatewayNo. have been writen into other parameters in demo.Please write down your value in following variate accordingly.
                    3、After payment, it will return XML
      ************************************************************************/
      $merNo            = ""; //MerchantNo.
 
      $gatewayNo        = ""; //GatewayNo.

      $signkey          = "1"; //SignKey

       
      $orderNo          = trim($_POST['orderNo']);
       
      $orderCurrency    = trim($_POST['orderCurrency']);

      $orderAmount      = trim($_POST['orderAmount']);

      $firstName        = trim($_POST['firstName']);

      $lastName         = trim($_POST['lastName']);

      $cardNo           = trim($_POST['cardNo']);

      $cardExpireYear   = trim($_POST['cardExpireYear']);

      $cardExpireMonth  = trim($_POST['cardExpireMonth']);

      $cardSecurityCode = trim($_POST['cardSecurityCode']);

      $email            = trim($_POST['email']);

      $issuingBank      = trim($_POST['issuingBank']);

      $phone            = trim($_POST['phone']);

      $ip               = trim($_POST['ip']);

      $signInfo         = hash("sha256" , $merNo . $gatewayNo . $orderNo . $orderCurrency . $orderAmount . $firstName . $lastName.$cardNo.$cardExpireYear.$cardExpireMonth.$cardSecurityCode.$email.$signkey );
       
      $country          = trim($_POST['country']);

      $state            = trim($_POST['state']);

      $city             = trim($_POST['city']);

      $address          = trim($_POST['address']);

      $zip              = trim($_POST['zip']);

      $returnUrl        = trim($_POST['returnUrl']); //real trading websites

      $csid             = trim($_POST['csid']);
     /****************************** 
       Submitting parameters by using curl and get returned XML parameters
      
     ****************************/
      $arr = array(
             'merNo'            => $merNo,            //MerchantNo.
             'gatewayNo'        => $gatewayNo,        //GatewayNo.
             'orderNo'          => $orderNo,          //OrderNo.
             'orderCurrency'    => $orderCurrency,    //OrderCurrency
             'orderAmount'      => $orderAmount,      //OrderAmount
             'firstName'        => $firstName,        //FirstName
             'lastName'         => $lastName,         //lastName
             'cardNo'           => $cardNo,           //CardNo
             'cardExpireMonth'  => $cardExpireMonth,  //CardExpireMonth
             'cardExpireYear'   => $cardExpireYear,   //CardExpireYear
             'cardSecurityCode' => $cardSecurityCode, //CVV
             'issuingBank'      => $issuingBank,      //IssuingBank
             'email'            => $email,            //EmailAddress
             'ip'               => $ip,               //ip
             'returnUrl'        => $returnUrl,          //real trading websites
             'phone'            => $phone,            //Phone Number 
             'country'          => $country,          //Country
             'state'            => $state,            //State
             'city'             => $city,             //City
             'address'          => $address,          //Address
             'zip'              => $zip,              //Zip Code
             'signInfo'         => $signInfo ,         //SignInfo 
             'csid'             => $csid
            );
      
       $data =  http_build_query($arr);
       
       $url  = "https://xx.com/TPInterface"; // Test Interface ---> https://xx.com/TestTPInterface  
      
       
            
            //===============================
                $curl = curl_init($url);
            curl_setopt($curl,CURLOPT_SSL_VERIFYPEER, 0);
            curl_setopt($curl,CURLOPT_HEADER, 0 ); // Colate HTTP header
            curl_setopt($curl,CURLOPT_RETURNTRANSFER, 1);// Show the output
            curl_setopt($curl,CURLOPT_POST,true); // Transmit datas by using POST
            curl_setopt($curl,CURLOPT_POSTFIELDS,$data);//Transmit datas by using POST
            curl_setopt($curl,CURLOPT_REFERER,$returnUrl);
            $xmlrs = curl_exec($curl);
            curl_close ($curl); 

            $xmlob = simplexml_load_string(trim($xmlrs));

            
            $merNo        = (string)$xmlob->merNo; //return merNo    
            $gatewayNo    = (string)$xmlob->gatewayNo;//return gatewayNo
            $tradeNo      = (string)$xmlob->tradeNo;//return tradeNo
            $orderNo      = (string)$xmlob->orderNo;//return orderno
            $orderAmount  = (string)$xmlob->orderAmount;//return orderAmount
            $orderCurrency= (string)$xmlob->orderCurrency;//return orderCurrency
            $orderStatus  = (string)$xmlob->orderStatus;//return orderStatus
            $orderInfo    = (string)$xmlob->orderInfo;//return orderInfo
            $signInfo     = (string)$xmlob->signInfo;//return signInfo
            $riskInfo     = (string)$xmlob->riskInfo;//return riskInfo
 
//===============================

            $xmlob = simplexml_load_string(trim($xmlrs));

            
            $merNo        = (string)$xmlob->merNo; //return merNo    
            $gatewayNo    = (string)$xmlob->gatewayNo;//return gatewayNo
            $tradeNo      = (string)$xmlob->tradeNo;//return tradeNo
            $orderNo      = (string)$xmlob->orderNo;//return orderno
            $orderAmount  = (string)$xmlob->orderAmount;//return orderAmount
            $orderCurrency= (string)$xmlob->orderCurrency;//return orderCurrency
            $orderStatus  = (string)$xmlob->orderStatus;//return orderStatus
            $orderInfo    = (string)$xmlob->orderInfo;//return orderInfo
            $signInfo     = (string)$xmlob->signInfo;//return signInfo
            $riskInfo     = (string)$xmlob->riskInfo;//return riskInfo

            //signInfocheck
            $signInfocheck=hash("sha256",$merNo.$gatewayNo.$tradeNo.$orderNo.$orderCurrency.$orderAmount.$orderStatus.$orderInfo.$signkey);

            //Returned signinfo of the encrypted string is capitalized, converted to lowercase, having returned encrypted signinfo string compare with the generated encrypted signainfo.
            if (strtolower($signInfo) == strtolower($signInfocheck)){

                  //if return success
                  if($orderStatus == "1"){
                        /* payment success,update orderInfo */
                        echo 'success'.$orderInfo;
                  }else{
                        /* payment fail,update orderInfo */
                       echo 'fail'.$orderInfo;
                  }
            }else{

                  //Encryption validate failure
                 echo 'fail'.$orderInfo;
            }




 ?>

