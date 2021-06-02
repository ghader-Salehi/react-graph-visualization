import React from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import CreateGraph from "../Layouts/CreateGraph";
import DataSetProccessPage from "../Layouts/UploadDataSet";
import {
  makeStyles,
  createMuiTheme,
  jssPreset,
  StylesProvider,
  ThemeProvider,
} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import { create } from "jss";
import rtl from "jss-rtl";
import NavBar from "../components/NavBar";
import { GraphWrapper } from "../context/GraphContext";

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  title: {
    flexGrow: 1,
  },
}));

function Index() {
  const jss = create({ plugins: [...jssPreset().plugins, rtl()] });
  const rtlTheme = createMuiTheme({ direction: "rtl" });
  const classes = useStyles();
  return (
    <Router>
      <div>
        <GraphWrapper>
          <ThemeProvider theme={rtlTheme}>
            <StylesProvider jss={jss}>
              <div className={classes.root}>
                <NavBar />
              </div>

              <Switch>
                <Route exact path="/" component={CreateGraph} />
                <Route
                  exact
                  path="/dataSetProccessing"
                  component={DataSetProccessPage}
                />
              </Switch>
            </StylesProvider>
          </ThemeProvider>
        </GraphWrapper>
      </div>
    </Router>
  );
}

export default Index;
