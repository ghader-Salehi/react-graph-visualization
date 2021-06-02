import React, { useState } from "react";
import { Graph } from "react-d3-graph";

const GraphComponent = ({ nodes, links , width , height  }) => {
  const myConfig = {
    nodeHighlightBehavior: true,
    height: height,
    width: width,
    maxZoom: 8,
    node: {
      color: "lightgreen",
      size: 200,
      highlightStrokeColor: "blue",
      // "renderLabel": false,
      fontSize: 7,
    },
    link: {
      highlightColor: "lightblue",
      renderLabel: true,
      fontSize: 8,
    },
  };

  return (
    <div className='border m-5'>
      <Graph
        id="graph-id" 
        data={{
          nodes,
          links
        }}
        config={myConfig}
      />
    </div>
  );
};

export default GraphComponent;
