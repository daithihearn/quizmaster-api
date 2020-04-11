// Get question by indexes
db.quizzes.aggregate([
    { $match: {_id: ObjectId('5e91bedf70416b47e5db30db')}}, 
    { $unwind: "$rounds"},
    { $match: {"rounds.index": 0}},
    { $unwind: "$rounds.questions"},
    { $match: {"rounds.questions.index": 0}},
    { $group: { _id: { question: "$rounds.questions"  } }},
])

db.quizzes.aggregate([
    { "$match" : { _id : ObjectId("5e91bedf70416b47e5db30db")}}, 
    { "$unwind" : "$rounds"} , 
    { "$match" : { "rounds.index" : 0}}, 
    { "$unwind" : "$rounds.questions"}, 
    { "$match" : { "rounds.questions.index" : 0}}, 
    { "$group" : { "_id" : "$rounds.questions"}}
])