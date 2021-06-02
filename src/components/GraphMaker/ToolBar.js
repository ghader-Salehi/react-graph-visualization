import {useState} from "react";
import { makeStyles, Button } from "@material-ui/core";
import clsx from "clsx";
import Source from '../../components/Modals/Source'
import Key from '../../components/Modals/Key'
import Link from '../../components/Modals/Links'
import Switch from '../../components/Modals/Switch'
import GraphWrapper from  '../../context/GraphContext';

const useStyles = makeStyles((theme) => ({

  fontStyle:{
    fontFamily:'iranyekan'
  }

}));
const ToolBar = () => {
  const classes = useStyles();
  const [isSourceModalOpen,setIsSourceModalOpen] = useState(false)
  const [isSwitchModalOpen,setIsSwitchModalOpen] = useState(false)
  const [isKeyModalOpen,setIsKeyModalOpen] = useState(false)
  const [isLinkModalOpen,setIsLinkModalOpen] = useState(false)


  const handleOpen=(item)=>{

    if(item === 'Source'){
      setIsSourceModalOpen(true);
    }else if(item === 'Switch'){
      setIsSwitchModalOpen(true);
    }else if(item === 'Key'){
      setIsKeyModalOpen(true);
    }else if(item === 'Link'){
      setIsLinkModalOpen(true);
    }
     
  }

  const handleClose = (item)=>{
    if(item === 'Source'){
      setIsSourceModalOpen(false);
    }else if(item === 'Switch'){
      setIsSwitchModalOpen(false);
    }else if(item === 'Key'){
      setIsKeyModalOpen(false);
    }else if(item === 'Link'){
      setIsLinkModalOpen(false);
    }
  }


  return (
    <>
      <div
        className={clsx([
          "d-flex flex-column align-items-start ",
          classes.font,
        ])}
      >
        <div className="p-3">
          <Button onClick={()=>handleOpen('Source')} className={classes.fontStyle}>منبع برق</Button>
        </div>
        <div className="p-3">
          <Button onClick={()=>handleOpen('Switch')} className={classes.fontStyle}>جعبه تقیسم</Button>
        </div>
        <div className="p-3">
          <Button onClick={()=>handleOpen('Key')} className={classes.fontStyle}>کلید</Button>
        </div>
        <div className="p-3">
          <Button onClick={()=>handleOpen('Link')} className={classes.fontStyle}>ایجاد اتصال</Button>
        </div>
      </div>

      
        <Source isOpen = {isSourceModalOpen} handleClose = {()=>handleClose('Source')} />
        <Switch isOpen = {isSwitchModalOpen} handleClose = {()=>handleClose('Switch')} />
        <Key isOpen = {isKeyModalOpen} handleClose = {()=>handleClose('Key')} />
        <Link isOpen = {isLinkModalOpen} handleClose = {()=>handleClose('Link')} />
     

     
     
    </>
  );
};

export default ToolBar;
