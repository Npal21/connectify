//console.log("script loaded");

let currentTheme=getTheme();
//console.log(currentTheme);

document.addEventListener("DOMContentLoaded",() => {
    changeTheme();
});



function changeTheme(){
    //set to web page
    changePageTheme(currentTheme, currentTheme);

    //set the listener to change theme button
    const changeThemeButton = document.querySelector('#theme_change_button');
    
    changeThemeButton.addEventListener("click",(event) => {
        let oldTheme = currentTheme;
        //console.log("change theme btn clicked");
        if (currentTheme === "dark"){
            currentTheme = "light";
        }
        else{
            currentTheme = "dark";
        }
        //console.log(currentTheme);
        changePageTheme(currentTheme, oldTheme);

      
    });
}

// Set theme to local storage
function setTheme(theme){
    localStorage.setItem("theme",theme);

}

//Get theme from local storage
function getTheme(){
    let theme = localStorage.getItem("theme");
    return theme ? theme : "dark";
}

function changePageTheme(theme, oldTheme){

     //update in localStorage
     setTheme(currentTheme);
     //remove current theme
     document.querySelector("html").classList.remove(oldTheme);
     
     //set currentTheme
     document.querySelector("html").classList.add(theme);
   
      //change button text
     document
        .querySelector('#theme_change_button')
        .querySelector("span").textContent = theme == "dark" ? "Light" : "Dark" ;
}