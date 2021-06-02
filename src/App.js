import Layout from "./Layouts";
import "./styles/globals.scss";
import { makeStyles, ThemeProvider, StylesProvider ,createMuiTheme,jssPreset } from "@material-ui/core";
import { create } from "jss";
import rtl from "jss-rtl";

const useStyles = makeStyles((theme) => ({
  fontStyle: {
    fontFamily: "iranyekan",
  },
}));
const App = () => {

  const classes = useStyles();

  const jss = create({ plugins: [...jssPreset().plugins, rtl()] });
  const rtlTheme = createMuiTheme({ direction: "rtl" });

  return (
    <>
      <ThemeProvider theme={rtlTheme}> 
        <StylesProvider jss={jss}>
          <div  className={classes.fontStyle}>
            <Layout />
          </div>
        </StylesProvider>
      </ThemeProvider>
    </>
  );
};

export default App;
