# sentimentAnalysis
sentiment analysis of product reviews
implementing paper (link : https://www.cs.uic.edu/~liub/publications/kdd04-revSummary.pdf)


step 1
breaking review into sentences because post tagger takes input sentence wise
breakReviewIntoSentence

step 2
now tagging the sentences using stanFord post tagger
TagText

step 3
generating set of nouns for doing association mining
creatingFeatureSetForAssociationMining

step 4
doing association mining using FPgrowth algorithm, results are saved in output file under package featureSetProductWise
FeatureGeneration

step 5
extract the opinion, those features which got opinions insert into potential feature table
opinionExtraction

step 6 
generating seedlist of adjectives which we can assign polarity
in our seedlist we took 30 adjectives for which we are sure, this 30 are top 30 frequency wise in our data
generatingSeedlistAdjective

step 7
finding polarity of remaining opinion with the help of wordnet
you have to install Wordnet software from their official site and give the installation directory while initializing
this program is an infinite loop stop after few minutes when your database is not changing(i am too lazy to handle infinite loop,sorry)
wordnet

step 8
insert the id of the adjectives in our database which have a negation word near them.
sentenceOrientation

step 9
generating score of each sentences
sentenceScore

step 10
generating feature wise score
featureScore

step 11(final step)
generating aggregate reviews in the text file under package ProductWiseOpinionSenntences
GeneratingProductWiseOpinionSentence
