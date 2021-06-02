import React from 'react'
import { Graph } from "react-d3-graph";

function Test() {

    const data = {
        nodes: [
          { id: "Source", color: "red" },
          { id: "Switch", color: "black" },
          { id: "k1", color: "green" },
          { id: "k2", color: "green" },
          { id: "k3", color: "green" },
          { id: "k4", color: "green" },
        ],
        links: [
          { source: "Source", target: "Switch", label: "4",color:'red' },
          { source: "Switch", target: "k1", label: "1"  ,color:'red' },
          { source: "Switch", target: "k2", label: "5"  },
          { source: "Switch", target: "k3", label: "6"  },
          { source: "Switch", target: "k4", label: "7"  ,color:'red' },
          { source: "k3", target: "k4", label: "9"  },
          { source: "k1", target: "k2", label: "2"  ,color:'red' },
          { source: "k2", target: "k3", label: "1"  ,color:'red' },
         
          
        ],
      };
      const data2 = {
        nodes: [{ id: "Harry", color: "black" }, { id: "Sally" }, { id: "Alice" }],
        links: [
          { source: "Harry", target: "Sally", label: "red" },
          { source: "Harry", target: "Alice", label: "test", color: "red" },
        ],
      };
    
      // the graph configuration, just override the ones you need
      const myConfig = {
        nodeHighlightBehavior: true,
        height: 250,
        width: 250,
        maxZoom: 8,
        node: {
          color: "lightgreen",
          size: 250,
          highlightStrokeColor: "blue",
          // "renderLabel": false,
        },
        link: {
          highlightColor: "lightblue",
          renderLabel: true,
          "fontSize": 8,
        },
        d3: { linkLength: 60, linkStrength: 1, gravity: -300, alphaTarget: 0.05 },
      };
    
      const onClickNode = function (nodeId) {
        // window.alert(`Clicked node ${nodeId}`);
      };
    
      const onClickLink = function (source, target) {
        // window.alert(`Clicked link between ${source} and ${target}`);
      };
    
      const handleaddClick = () => {
        data.nodes.push({ id: "ghader", color: "black" });
        data.links.push({ source: "5455", target: "ghader" });
      };
    
      const onZoomChange = function (previousZoom, newZoom) {
        // window.alert(`Graph is now zoomed at ${newZoom} from ${previousZoom}`);
      };
    
      const onNodePositionChange = function (nodeId, x, y) {
        // window.alert(`Node ${nodeId} moved to new position x= ${x} y= ${y}`);
      };

    return (
        <>
        <Graph
        id="graph-id" // id is mandatory
        data={data}
        config={myConfig}
        onClickNode={onClickNode}
        onClickLink={onClickLink}
        onNodePositionChange={onNodePositionChange}
        onZoomChange={onZoomChange}
      />
        </>
    )
}

export default Test
