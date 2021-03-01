import React, {Component} from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  TextInput,
  TouchableOpacity,
  Alert,
  Linking,
  Image,
} from 'react-native';

import {KeypadView} from '@components';
import {image} from '@constants';

import {
  check,
  PERMISSIONS,
  RESULTS,
  checkMultiple,
  requestMultiple,
} from 'react-native-permissions';
import ReplaceDialer from 'react-native-replace-dialer';

import {responsiveWidth, responsiveHeight, responsiveFonts} from '@resources';

export default class DialScreen extends Component<{}> {
  state = {
    phoneNumber: '',
    speakerStatus: false,
  };
  componentDidMount() {
    ReplaceDialer.isDefaultDialer((data) => {
      console.log('isDefaultDialer>>', data);
      if (data) {
        console.log('Is ALREADY default dialer.');
      } else {
        console.log('Is NOT default dialer, try to set.');
        ReplaceDialer.setDefaultDialer((data) => {
          console.log('setDefaultDialer>>', data);
          if (data) {
            console.log('Default dialer sucessfully set.');
          } else {
            console.log('Default dialer NOT set');
          }
        });
        this.checkPermissions().then((statuses) => {});
      }
    });
  }

  requestPermission() {
    return new Promise(function (resolve, reject) {
      requestMultiple([PERMISSIONS.ANDROID.CALL_PHONE]).then((statuses) => {
        resolve(statuses);
      });
    });
  }

  checkPermissions() {
    return new Promise(function (resolve, reject) {
      checkMultiple([PERMISSIONS.ANDROID.CALL_PHONE]).then((statuses) => {
        resolve(statuses);
      });
    });
  }

  onPressCall() {
    const {phoneNumber} = this.state;
    const {navigate} = this.props.navigation;
    this.checkPermissions().then((statuses) => {
      console.log('statussesssss>>', statuses);
      switch (statuses['android.permission.CALL_PHONE']) {
        case RESULTS.GRANTED:
          navigate('CallScreen', {phoneNumber: phoneNumber});
          break;
        case RESULTS.BLOCKED:
          Alert.alert(
            'Error',
            'Please allow this app the call permission in settings.',
            [
              {
                text: 'OK',
                onPress: () => Linking.openSettings(),
              },
            ],
          );
          break;
        case RESULTS.DENIED:
          this.requestPermission();
          break;
        case RESULTS.UNAVIALABLE:
          Alert.alert('Error', 'Call feature is not avaiable on this device.');
          break;
      }
    });
  }

  handleKeypadPress = (value) => {
    console.log(value);
    this.setState((prevState) => ({
      phoneNumber: prevState.phoneNumber.concat(value),
    }));
  };

  onPressClear = () => {
    this.setState((prevState) => ({
      phoneNumber: prevState.phoneNumber.slice(0, -1),
    }));
  };

  render() {
    const {phoneNumber} = this.state;
    return (
      <View style={styles.container}>
        <View>
          <Text style={styles.input}>{this.state.phoneNumber}</Text>
        </View>

        <KeypadView
          onKeypadPress={(value) => {
            this.handleKeypadPress(value);
          }}
        />

        <View
          style={{
            width: '100%',
            flexDirection: 'row',
            justifyContent: 'center',
          }}>
          <TouchableOpacity
            style={{flex: 6}}
            onPress={() => this.onPressCall()}>
            <Image
              style={styles.callBtn}
              source={{uri: image.recieveCallButton}}
            />
          </TouchableOpacity>
          <TouchableOpacity
            style={{flex: 4}}
            onPress={() => this.onPressClear()}>
            <Image style={styles.clearBtn} source={{uri: image.clear_symbol}} />
          </TouchableOpacity>
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  input: {
    marginTop: responsiveHeight(25),
    width: responsiveWidth(90),
    borderWidth: 1,
    borderColor: 'lightgray',
    borderRadius: 5,
    fontSize: responsiveFonts(35),
    textAlign: 'center',
    padding: 10,
    color: 'gray',
  },
  callBtn: {
    aspectRatio: 1,
    height: '28%',
    alignSelf: 'flex-end',
  },
  clearBtn: {
    marginTop: 10,
    aspectRatio: 1,
    height: '15%',
    alignSelf: 'center',
  },
  callText: {
    fontSize: 18,
    padding: 10,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-evenly',
    width: '100%',
  },
});
