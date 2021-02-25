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
} from 'react-native';

import {
  check,
  PERMISSIONS,
  RESULTS,
  checkMultiple,
  requestMultiple,
} from 'react-native-permissions';
import ReplaceDialer from 'react-native-replace-dialer';

export default class AppContainer extends Component<{}> {
  state = {
    phoneNumber: '666',
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
    this.checkPermissions().then((statuses) => {
      console.log('statussesssss>>', statuses);
      switch (statuses['android.permission.CALL_PHONE']) {
        case RESULTS.GRANTED:
          ReplaceDialer.callPhoneNumber(phoneNumber, (message) => {
            console.log(message);
          });
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

  render() {
    const {phoneNumber} = this.state;
    return (
      <View style={styles.container}>
        <Text>Enter Phone Number</Text>
        <TextInput
          style={styles.input}
          keyboardType="numeric"
          value={phoneNumber}
        />
        <TouchableOpacity
          style={styles.callBtn}
          onPress={() => this.onPressCall()}>
          <Text style={styles.callText}>Call</Text>
        </TouchableOpacity>
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
    borderWidth: 1,
    borderColor: 'gray',
    borderRadius: 5,
    width: '90%',
  },
  callBtn: {
    borderWidth: 2,
    borderColor: 'green',
    borderRadius: 5,
    marginTop: 10,
  },
  callText: {
    fontSize: 18,
    padding: 10,
  },
});

export * from '@components';
export * from '@constants';
