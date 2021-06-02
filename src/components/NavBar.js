import { AppBar, Button,Toolbar,makeStyles } from "@material-ui/core";
import clsx from 'clsx'
import { useHistory }from 'react-router-dom'


const useStyles = makeStyles((theme)=>({
    fontStyle:{
        fontFamily:'iranyekan'
    }
}))
const NavBar = () => {
  const classes = useStyles()
  const history = useHistory();

  
  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Button className={classes.fontStyle} color="inherit" onClick={()=>{history.push('/')}} >تولید گراف</Button>
          <Button className={clsx([classes.fontStyle,'mr-5'])} color="inherit" onClick={()=>{history.push('/dataSetProccessing')}}>آپلود دیتاست</Button>
        </Toolbar>
      </AppBar>
    </>
  );
};

export default NavBar;
