import axios from "axios"


export const createGraph = async (data)=>{
    return await axios.post('',data);
}

export const uploadData = async (img)=>{
    return await axios.post('',img,{
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}