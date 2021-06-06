import React from 'react'
// import Dst from "../../assets/icons/icons8-junction-80.png";
function Index(props) {
    const canvasRef = React.useRef(null)

    React.useEffect(()=>{
        const canvas =  canvasRef.current;
        const context = canvas.getContext('2d');
        const base_image = new Image();
        base_image.src=props.img;
        base_image.width = 1000
        base_image.height = 1000
        
        context.drawImage(base_image, 0, 0);
    //    
       
    },[])
    return (
        <>
        <canvas  ref={canvasRef} />
        </>
    )
}

export default Index
