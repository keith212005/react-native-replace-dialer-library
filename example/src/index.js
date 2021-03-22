import React from 'react';
import {LogBox} from 'react-native';
import {AppContainer} from './navigator';

export default class App extends React.Component {
  render() {
    const {initialScreenName} = this.props;
    console.log('screenname>>>', initialScreenName);
    LogBox.ignoreLogs(['Warning: ...']);
    LogBox.ignoreAllLogs();
    return <AppContainer screenName={initialScreenName} />;
  }
}
