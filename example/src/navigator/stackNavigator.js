import React from 'react';
import {StatusBar} from 'react-native';

import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';

import {responsiveHeight} from '@resources';
import * as Screen from '@screen';

const Stack = createStackNavigator();

export default class AppContainer extends React.Component {
  _addScreen = (routeName, isNavigator = false, extraProps = {}) => (
    <Stack.Screen
      name={routeName}
      component={!isNavigator ? Screen[routeName] : undefined}
      {...extraProps}
    />
  );

  render(props) {
    console.log('StackNavScree>>>', this.props);
    return (
      <NavigationContainer>
        <Stack.Navigator
          initialRouteName={this.props.screenName}
          screenOptions={{
            headerShown: false,
            gesturesEnabled: false,
            animationEnabled: false,
          }}>
          {this._addScreen('DialScreen')}
          {this._addScreen('CallScreen')}
        </Stack.Navigator>
      </NavigationContainer>
    );
  }
}
