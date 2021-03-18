import React from 'react';
import {StatusBar, View} from 'react-native';

import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';

import {responsiveHeight} from '@resources';
import * as Screen from '@screen';

const Stack = createStackNavigator();

export default class AppContainer extends React.Component {
  _addScreen = (
    routeName,
    isNavigator = false,
    extraProps = {},
    initialParams,
  ) => (
    <Stack.Screen
      name={routeName}
      component={!isNavigator ? Screen[routeName] : undefined}
      initialParams={initialParams}
      {...extraProps}
    />
  );

  render(props) {
    return (
      <>
        <StatusBar
          backgroundColor="transparent"
          barStyle={'dark-content'}
          translucent={true}
        />
        <NavigationContainer>
          <Stack.Navigator
            initialRouteName={
              this.props.screenName ? this.props.screenName : 'DialScreen'
            }
            screenOptions={{
              headerShown: false,
              gesturesEnabled: false,
              animationEnabled: false,
            }}>
            {this._addScreen('DialScreen')}
            {this._addScreen('CallScreen', false, null, {})}
          </Stack.Navigator>
        </NavigationContainer>
      </>
    );
  }
}
