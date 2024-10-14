console.log("Contacts.js")

//const baseURL = "http://connectify.ap-south-1.elasticbeanstalk.com";
const baseURLs = ["http://www.linktact.site",
                  "http://main.linktact.site",
                  "http://linktact.site",
                  "http://connectify.ap-south-1.elasticbeanstalk.com",
                  "http://localhost:8081"
                  ];


// Function to get the appropriate base URL based on the current hostname
function getBaseURL() {
  const currentHost = window.location.hostname;
  if (currentHost.includes('main')) {
      return baseURLs[1]; 
  } else if (currentHost.includes('www')) {
      return baseURLs[0]; 
  }else if (currentHost.includes('connectify')) {
    return baseURLs[3]; 
  }else if (currentHost.includes('localhost')) {
       return baseURLs[4];
  }else {
      return baseURLs[2]; 
  }
}

// Use the selected base URL for API calls
const baseURL = getBaseURL();

// set the modal menu element
const viewContactModal = document.getElementById('view_contact_modal');

// options with default values
const options = {
    placement: 'bottom-right',
    backdrop: 'dynamic',
    backdropClasses:
        'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
    closable: true,
    onHide: () => {
        console.log('modal is hidden');
    },
    onShow: () => {
        console.log('modal is shown');
    },
    onToggle: () => {
        console.log('modal has been toggled');
    },
};

// instance options object
const instanceOptions = {
    id: 'view_contact_modal',
    override: true
  };

  const contactModal = new Modal(viewContactModal, options, instanceOptions);

  function openContactModal(){
    contactModal.show();
  }

  function closeContactModal(){
    contactModal.hide();
  }

  async function loadContactData(id){
    //function call to load data
    console.log("id=",id);
    try{
        const data = await(await fetch(`${baseURL}/api/contacts/${id}`))
                    .json();
        console.log(data);

        document.querySelector("#contact_name").innerHTML = data.name;
        document.querySelector("#contact_email").innerHTML = data.email;
        document.querySelector("#contact_image").src = data.picture;
        document.querySelector("#contact_address").innerHTML = data.address;
        document.querySelector("#contact_phone").innerHTML = data.phoneNumber;
        document.querySelector("#contact_about").innerHTML = data.description;
        const contactFavourite = document.querySelector("#contact_favourite");
        if(data.favourite){
            contactFavourite.innerHTML = 
            "<i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i>"
        }else{
            contactFavourite.innerHTML = "Not favourite contact";
        }
        document.querySelector("#contact_website").href = data.websiteLink;
        document.querySelector("#contact_website").innerHTML = data.websiteLink;
        document.querySelector("#contact_linkedIn").href = data.linkedInLink;
        document.querySelector("#contact_linkedIn").innerHTML = data.linkedInLink;     

        openContactModal();
    }catch(error){
        console.log("Error", error);
    }

  }

  async function deleteContact(id){
    Swal.fire({
        title: "Do you want to delete the contact?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Delete",
        customClass: {
            confirmButton: 'btn-delete',
            cancelButton: 'btn-cancel'
        }
      }).then((result) => {        
        if (result.isConfirmed) {
          const url = `${baseURL}/user/contacts/delete/`+id;
          window.location.replace(url);
        }
      });

  }















