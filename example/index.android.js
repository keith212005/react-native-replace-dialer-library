import {AppRegistry} from 'react-native';
import App from './src';
import CallActivity from './callActivity';
import {name as appName} from './app.json';

// AppRegistry.registerComponent(appName, () => CallActivity);
AppRegistry.registerComponent(appName, () => App);
