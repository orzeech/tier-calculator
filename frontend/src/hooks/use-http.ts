import {useCallback, useReducer} from 'react';

type State = {
  data?: any;
  error?: string;
  status?: string;
}

type Action =
    | { type: 'SEND' }
    | { type: 'SUCCESS', responseData: any }
    | { type: 'ERROR', errorMessage: string };

function httpReducer(state: State, action: Action): State {
  if (action.type === 'SEND') {
    return {
      data: null,
      status: 'pending',
    };
  }
  if (action.type === 'SUCCESS') {
    return {
      data: action.responseData,
      status: 'completed',
    };
  }
  if (action.type === 'ERROR') {
    return {
      data: null,
      error: action.errorMessage,
      status: 'completed',
    };
  }
  return state;
}

function useHttp(requestFunction: Function, startWithPending = false) {
  let action: State = {
    status: startWithPending ? 'pending' : undefined,
    data: null,
    error: undefined,
  };
  const [httpState, dispatch] = useReducer(httpReducer, action);

  const sendRequest = useCallback(
      async function (requestData) {
        dispatch({type: 'SEND'});
        try {
          const responseData = await requestFunction(requestData);
          dispatch({type: 'SUCCESS', responseData});
        } catch (error: any) {
          dispatch({
            type: 'ERROR',
            errorMessage: error.message || 'Something went wrong!',
          });
        }
      },
      [requestFunction]
  );

  return {
    sendRequest,
    ...httpState,
  };
}

export default useHttp;
