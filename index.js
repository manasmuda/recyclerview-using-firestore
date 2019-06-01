const functions = require('firebase-functions');
const admin=require('firebase-admin');

admin.initializeApp();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.restrictuser=functions.firestore.document('USER/{userid}').onCreate((change,context)=>{
    const newValue=change.data();
    admin.firestore().collection('USER').get().then(documentSet=>{
        const ls=documentSet.size;
        if(ls>10){
            admin.firestore().collection('USER').orderBy('createdDate').limit(ls-10).get().then(ordereddoc=>{
                ordereddoc.forEach((doc) => {
                    admin.firestore().collection('USER').doc(doc.id).delete();
                    //console.log(doc.id, '=>', doc.data());
                  });
                return 0;
            }).catch(err=>{
                console.log('Error',err);
            });
        }
        return ls;
    }).catch(err=>{
        console.log('Error',err);
    });
    return 0;
});