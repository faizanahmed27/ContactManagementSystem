console.log("This is script file");

const toggleSidebar=()=>{

    if($(".sidebar").is(":visible")){

        //true
        //band karna hai

        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");

    }else{

        //false
        //show karna hai

        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }

};

const search=() =>{
    console.log("Searching...");

    let query=$("#search-input").val();
   

    // show names in box

    if(query==''){

    }else{

        // search
        console.log(query);

        // sending request to server

        let url = `http://localhost:8080/search/${query}`;

        fetch(url).then(Response=>{

            return Response.json();

        }).then(data=>{

            // data...
            console.log(data);

            let text=`<div class='list-group'>`;

            data.forEach(contact=>{

                text+=`<a href='/user/contact/${contact.cid}' class='list-group-item list-group-action'>${contact.name} </a>`
            });

            text += `</div>`;

            $(".search-result").html(text);
            $(".search-result").show();

        });


       
    }
};

// razorpay client side integrated
// first request - to server to create order

const paymentStart = () =>{
    console.log("Payment has been started...");
    let amount = $("#payment_id").val();
    console.log(amount);
    if(amount=='' || amount==null){
        //alert("Amount can not be blank or null");
        swal("Failed !!", "Amount can not be blank or null !!", "error");
        return;
    }

    // code
    // we will use ajax to send request to server to create the order - use jquery ajax
    $.ajax({
        url: "/user/create/order",
        data: JSON.stringify({ amount: amount, info:"order_request" }),
        contentType:"application/json",
        type:"POST",
        dataType:"json",
        success:function(response){
            // invoked when success
            console.log(response);
            if(response.status=="created"){
                // open payment form
                let options={
                    key:'rzp_test_MTN0sptGOn4ror',
                    amount:response.amount,
                    currency:response.currency,
                    name:"Holiday Shope",
                    description:"Payment",
                    image:"https://holidayshope.com/public/logo.png",
                    order_id:response.id,
                    // handler function
                    handler:function(response){
                        console.log(response.razorpay_payment_id);
                        console.log(response.razorpay_order_id);
                        console.log(response.razorpay_signature);
                        console.log('Payment successful !!');
                        //alert("Congrats !! Payment successfull !!");
                        swal("Good job!", "Congrats !! Payment successfull !!", "success");

                    },
                    "prefill": {
                        "name": "",
                        "email": "",
                        "contact": ""
                    },
                    "notes": {
                        "address": "Holiday Shope.COM"
                    },
                    "theme": {
                        "color": "#3399cc"
                    },

                };

                let rzp=new Razorpay(options);
                rzp.on('payment.failed', function (response){
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
                    //alert("Oops payment failed !!");
                    swal("Failed !!", "Oops payment failed !!", "error");
            });

                rzp.open();
            }
        },
       error:function(error){
            // invoked when error
            console.log(error);
            alert("Something went wrong !!");
        }
    })
}