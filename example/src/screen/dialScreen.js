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
  request,
} from 'react-native-permissions';
import ReplaceDialer from 'react-native-replace-dialer';

import {responsiveWidth, responsiveHeight, responsiveFonts} from '@resources';

const permissions = [
  PERMISSIONS.ANDROID.CALL_PHONE,
  PERMISSIONS.ANDROID.READ_CALL_LOG,
  PERMISSIONS.ANDROID.RECORD_AUDIO,
  PERMISSIONS.ANDROID.WRITE_EXTERNAL_STORAGE,
];

export default class DialScreen extends Component<{}> {
  constructor(props) {
    super(props);
  }
  state = {
    phoneNumber: '9409005997',
    speakerStatus: false,
    alertShow: false,
    alertMessage: '',
    shouldGoToSetting: false,
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
      }
    });

    // this.checkPermissions();
  }

  requestMultiplePermissions() {
    requestMultiple(permissions).then((statuses) => {
      resolve(statuses);
    });
  }

  checkPermissions() {
    checkMultiple(permissions).then((statuses) => {
      var size = Object.keys(statuses).length;
      console.log(statuses);
      console.log(size);
      for (var i = 0; i < permissions.length; i++) {
        switch (statuses[permissions[i]]) {
          case RESULTS.UNAVAILABLE:
            console.log('This feature is not available on this device.');
            break;
          case RESULTS.DENIED:
            console.log(
              'The permission has not been requested / is denied but requestable',
            );
            this.requestMultiplePermissions();
            break;
          case RESULTS.LIMITED:
            console.log('The permission is limited: some actions are possible');
            break;
          case RESULTS.GRANTED:
            console.log('The permission is granted');
            break;
          case RESULTS.BLOCKED:
            break;
        }
      }
    });
  }

  onPressCall() {
    const {phoneNumber} = this.state;
    const {navigate} = this.props.navigation;
    request(permissions[0]).then((result) => {
      switch (result) {
        case RESULTS.GRANTED:
          {
            ReplaceDialer.callPhoneNumber(phoneNumber, (message) => {
              console.log(message);
            });
          }
          break;
        case RESULTS.BLOCKED:
          Alert.alert(
            'Call Permissions',
            'Allow this app to make phone calls. Please allow Phone permission in the settings.',
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
          <TextInput
            style={styles.input}
            value={this.state.phoneNumber}
            showSoftInputOnFocus={false}
          />
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
            <Image style={styles.clearBtn} source={{uri: image.clear_b}} />
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
