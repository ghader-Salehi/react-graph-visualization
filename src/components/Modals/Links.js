import React, { useContext, useState } from "react";
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

const Links = ({ isOpen, handleClose }) => {
  const [state, dispatch] = useContext(GraphContext);
  const classes = useStyles();

  const [dpStart, setdpStart] = React.useState("");
  const [dpEnd, setdpEnd] = React.useState("");
  const [linkWeight, setLinkWeight] = useState("");


  const handleChangeStart = (event) => {
    setdpStart(event.target.value);

  };

  const handleChangeEnd = (event) => {
    setdpEnd(event.target.value);
  };

  const handleAddLink = ()=>{
    handleClose();
    dispatch({
      type: "ADD_LINK",
      payload: {
        source: dpStart,
        target: dpEnd,
        label: linkWeight,
      },
    });
  }


  return (
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
              افزودن اتصال  
            </div>
            <div className="d-flex m-3">
              <div className="m-2">
                <span> مبدا : </span>
              </div>
              <div>
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
                    value={dpStart}
                    onChange={handleChangeStart}
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
            </div>

            <div className="d-flex m-3">
              <div className="mt-3 ml-2">
                <span> مقصد : </span>
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
                    value={dpEnd}
                    onChange={handleChangeEnd}
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
                <TextField
                  type="number"
                  onChange={(e) => setLinkWeight(e.target.value)}
                />
              </div>
            </div>
            <div className="d-flex justify-content-center  m-3">
              <Button
                variant="contained"
                color="primary"
                className={clsx([classes.fontStyle])}
                onClick={handleAddLink}
              >
                افزودن
              </Button>
            </div>
          </div>
        </Fade>
      </Modal>
    </>
  );
};

export default Links;
