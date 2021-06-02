import {createContext,useReducer,useState} from 'react';

 const GraphContext = createContext();

const initialState = {
    nodes:[],
    links:[]
}


const reducer = (state, action) => {
    switch (action.type) {
      case "ADD_SOURCE":
        return {
          ...state,
          nodes:[...state.nodes,action.payload]
        };
      case "ADD_SWITCH":
        return {
          ...state,
          nodes:[...state.nodes,action.payload]
        };
      case "ADD_KEY":
        return {
          ...state,
          nodes:[...state.nodes,action.payload]
        };
      case "ADD_LINK":
        return {
          ...state,
          links:[...state.links,action.payload]
        };

      default:
        throw new Error();
    }
  };
// Wrapper
const GraphWrapper  = ({children}) => {
    const [state , dispatch] = useReducer(reducer,initialState)

    return(
        <GraphContext.Provider value={[state , dispatch]}>
            {children}
        </GraphContext.Provider>
    )

    
}

export {GraphWrapper,GraphContext}  ;