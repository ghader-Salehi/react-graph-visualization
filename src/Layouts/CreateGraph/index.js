import React, { useState,useContext,useEffect } from "react";
import { Button, makeStyles } from "@material-ui/core";
import TollBar from "../../components/GraphMaker/ToolBar";
import clsx from "clsx";
import Graph from "../../components/GraphMaker/GraphComponent";
import {GraphWrapper}  from "../../context/GraphContext";
import {GraphContext} from '../../context/GraphContext'
import {createGraph} from  '../../api/graph'
const useStyle = makeStyles((theme) => ({
  paper: {
    marginRight: theme.spacing(2),
  },
  container: {
    marginTop: "11%",
  },
  fontStyle:{
    fontFamily:'iranyekan'
}
}));

const Index = () => {
  const classes = useStyle();
  const [state , dispatch] = useContext(GraphContext);
  // const [nodes, setNodes] = useState([]);
  // const [links, setLinks] = useState([]);
  const [showProccessedGraph,setShowProccessedGraph] = useState(false)
  //  it will be fill after proccess
  const [coloredLinks, setColoredLinks] = useState([]);



  const handleProccessOnGraph = () => {};
  React.useEffect(()=>{
    console.log(state);
  })

  const handleProccess = ()=>{
    createGraph(state)
        .then((res)=>{

        }).catch(()=>{

        })
        
    setShowProccessedGraph(true);
  }

  useEffect(()=>[
      console.log(state)
  ],[])
  return (
   
      <div className='d-flex flex-column'>
        <div className="d-flex">
          <div className={clsx(["col-2", classes.container])}>
            <TollBar />
          </div>
          <div className={clsx(['col-9 d-flex justify-content-center  mt-5 pb-3',classes.container])}>
            <Graph width={600} height={350} nodes={state.nodes} links ={showProccessedGraph ? [] : state.links } />
            {/* <Graph width={400} height={300} nodes={state.nodes} links ={state.links} /> */}
            {/* */}
          </div>
        </div>
        <div className='d-flex justify-content-center mt-2'>
          <Button onClick={handleProccess} variant="contained" color="primary" className={classes.fontStyle}>
            پردازش
          </Button>
        </div>
      </div>
    
  );
};

export default Index;
