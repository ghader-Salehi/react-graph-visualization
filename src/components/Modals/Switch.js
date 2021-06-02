import React,{useState,useContext} from  'react';
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";
import Modal from "@material-ui/core/Modal";
import Backdrop from "@material-ui/core/Backdrop";
import { useSpring, animated } from "react-spring";
import { GraphContext } from "../../context/GraphContext";
import clsx from "clsx";
import {
  ThemeProvider,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  createMuiTheme,
} from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  modal: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  paper: {
    backgroundColor: theme.palette.background.paper,
    boxShadow: theme.shadows[5],
    padding: theme.spacing(2, 4, 3),
  },
  fontStyle: {
    fontFamily: "iranyekan",
  },
  formControl: {
    minWidth: 200,
  },
  selectEmpty: {
    marginTop: theme.spacing(2),
  },
}));

const Fade = React.forwardRef(function Fade(props, ref) {
  const { in: open, children, onEnter, onExited, ...other } = props;
  const style = useSpring({
    from: { opacity: 0 },
    to: { opacity: open ? 1 : 0 },
    onStart: () => {
      if (open && onEnter) {
        onEnter();
      }
    },
    onRest: () => {
      if (!open && onExited) {
        onExited();
      }
    },
  });

  return (
    <animated.div ref={ref} style={style} {...other}>
      {children}
    </animated.div>
  );
});
Fade.propTypes = {
  children: PropTypes.element,
  in: PropTypes.bool.isRequired,
  onEnter: PropTypes.func,
  onExited: PropTypes.func,
};

const Switch = ({ isOpen ,handleClose })=>{
    const [state, dispatch] = useContext(GraphContext);
    const classes = useStyles();
    const [dp, setdp] = React.useState("");
    const [currentSwitchName, setCurrentSwitchName] = useState("switch0");
    const [linkDest, setLinkDest] = useState("");
    const [linkWeight,setLinkWeight] = useState('');

    const handleChange = (event) => {
      setdp(event.target.value);
      setLinkDest(event.target.value);
    };

    const handleAddNode = () => {
      handleClose();
      dispatch({
        type: "ADD_SWITCH",
        payload: { id: currentSwitchName, color: "green" },
      });
      if (linkDest) {
        dispatch({
          type: "ADD_LINK",
          payload: { source: currentSwitchName, target: linkDest, label: linkWeight },
        });
      }
      setLinkDest("");
      setdp("");
      setCurrentSwitchName(`switch${state.nodes.length + 1}`)
    };

    return(
        <>
        <Modal
        aria-labelledby="spring-modal-title"
        aria-describedby="spring-modal-description"
        className={classes.modal}
        open={isOpen}
        onClose={handleClose}
        closeAfterTransition
        BackdropComponent={Backdrop}
        BackdropProps={{
          timeout: 500,
        }}
      >
        <Fade in={isOpen}>
        <div className={clsx([classes.paper, classes.fontStyle])}>
          <div className="d-flex justify-content-center">
          افزودن جعبه تقسیم
          </div>
            <div className="d-flex m-3">
              <div className="m-2">
                <span> نام گره : </span>
              </div>
              <div>
                <TextField
                  value={currentSwitchName}
                  onChange={(e) => currentSwitchName(e.target.value)}
                />
              </div>
            </div>

            <div className="d-flex m-3">
              <div className="mt-3 ml-2">
                <span> اتصال به : </span>
              </div>
              <div className="d-flex ">
                <FormControl className={classes.formControl}>
                  <InputLabel
                    className={clsx([classes.fontStyle])}
                    id="demo-simple-select-label"
                  >
                    یک گره انتخاب کنید
                  </InputLabel>
                  <Select
                    labelId="demo-simple-select-label"
                    id="demo-simple-select"
                    value={dp}
                    onChange={handleChange}
                    className={clsx([classes.fontStyle])}
                  >

                    {state.nodes.length !== 0 ? (
                      state.nodes.map((item, index) => {
                        return (
                          <MenuItem
                            className={clsx([classes.fontStyle])}
                            value={item.id}
                          >
                            {item.id}
                          </MenuItem>
                        );
                      })
                    ) : (
                      <MenuItem
                        disabled
                        className={clsx([classes.fontStyle])}
                        value={0}
                      >
                        آیتمی وجود ندارد
                      </MenuItem>
                    )}
                  </Select>
                </FormControl>
                
              </div>
              
            </div>
            <div className="d-flex mt-4 m-3">
                  <div className="m-2">
                    <span> وزن یال : </span>
                  </div>
                  <div>
                    <TextField type='number'  onChange={(e) => setLinkWeight(e.target.value)} />
                  </div>
                </div>
            <div className="d-flex justify-content-center  m-3">
              <Button
                variant="contained"
                color="primary"
                className={clsx([classes.fontStyle])}
                onClick={handleAddNode}
              >
                افزودن
              </Button>
            </div>
          </div>
        </Fade>
      </Modal>
        </>
    )
}

export default Switch;