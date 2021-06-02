import { Checkbox } from "@material-ui/core";
import React from "react";

const Index = () => {
  const [checked, setChecked] = React.useState(false);

  const handleChange = (event) => {
    setChecked(event.target.checked);
  };
  const handleAddFile = (e)=>{
    console.log(e.target.files[0]);
  }
  return (
    <>
      <div className="d-flex">
        <div className=" m-3">
          <input onChange={handleAddFile} type="file" />
        </div>
        <div className="d-flex">
            <div className='m-3 mt-4'>
                <span>
                    پردازش بدون جعبه تقیسم در دو طرف در 
                </span>
            </div>
          <Checkbox
            className='p-3'
            checked={checked}
            onChange={handleChange}
            color="primary"
            inputProps={{ 'aria-label': 'secondary checkbox' }}
          />
        </div>
      </div>
    </>
  );
};

export default Index;
